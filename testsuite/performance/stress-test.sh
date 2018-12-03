#!/bin/bash

# Execution Parameters
mvn="${MVN:-mvn}"
keycloakProjectHome=${KEYCLOAK_PROJECT_HOME:-$(cd "$(dirname "$0")/../.."; pwd)}
dryRun=${DRY_RUN:-false}

# Performance Testsuite Parameters
dataset=${DATASET:-1r_10c_100u}
warmUpPeriod=${WARMUP_PERIOD:-120}
rampUpPeriod=${RAMPUP_PERIOD:-60}
measurementPeriod=${MEASUREMENT_PERIOD:-120}
filterResults=${FILTER_RESULTS:-true}

# Stress Test Parameters
algorithm=${STRESS_TEST_ALGORITHM:-incremental}
maxIterations=${STRESS_TEST_MAX_ITERATIONS:-10}
provisioning=${STRESS_TEST_PROVISIONING:-false}
generateDataset=${STRESS_TEST_PROVISIONING_GENERATE_DATASET:-false}
provisioningParameters=${STRESS_TEST_PROVISIONING_PARAMETERS:-}

# Stress Test - Incremental Algorithm Parameters
usersPerSec0=${STRESS_TEST_UPS_FIRST:-1.000}
incrementFactor=${STRESS_TEST_UPS_INCREMENT:-1.000}

# Stress Test - Bisection Algorithm Parameters
lowerBound=${STRESS_TEST_UPS_LOWER_BOUND:-0.000}
upperBound=${STRESS_TEST_UPS_UPPER_BOUND:-10.000}
resolution=${STRESS_TEST_UPS_RESOLUTION:-1.000}

if $generateDataset; then datasetProfile="generate-data"; else datasetProfile="import-dump"; fi
provisionCommand="$mvn -f $keycloakProjectHome/testsuite/performance/pom.xml verify -P provision,$datasetProfile $provisioningParameters -Ddataset=$dataset"
teardownCommand="$mvn -f $keycloakProjectHome/testsuite/performance/pom.xml verify -P teardown"

function runCommand {
    echo "  $1"
    echo 
    if ! $dryRun; then eval "$1"; fi
}

function runTest {

    if [[ $i == 0 || $provisioning == true ]]; then # use specified warmUpPeriod only in the first iteration, or if provisioning is enabled
        warmUpParameter="-DwarmUpPeriod=$warmUpPeriod ";
    else 
        warmUpParameter="-DwarmUpPeriod=0 ";
    fi

    testCommand="$mvn -f $keycloakProjectHome/testsuite/performance/tests/pom.xml verify -Ptest $@ -Ddataset=$dataset $warmUpParameter -DrampUpPeriod=$rampUpPeriod -DmeasurementPeriod=$measurementPeriod -DfilterResults=$filterResults -DusersPerSec=$usersPerSec"

    if $provisioning; then 
        runCommand "$provisionCommand"
        if [[ $? != 0 ]]; then
            echo "Provisioning failed."
            runCommand "$teardownCommand" || break
            break
        fi
        runCommand "$testCommand"
        export testResult=$?
        runCommand "$teardownCommand" || exit 1
    else
        runCommand "$testCommand"
        export testResult=$?
    fi

    [[ $testResult != 0 ]] && echo "Test exit code: $testResult"

}

echo "Stress Test Summary:"
echo
echo "Script Execution Parameters:"
echo "  MVN: $mvn"
echo "  KEYCLOAK_PROJECT_HOME: $keycloakProjectHome"
echo "  DRY_RUN: $dryRun"
echo
echo "Performance Testsuite Parameters:"
echo "  DATASET: $dataset"
echo "  WARMUP_PERIOD: $warmUpPeriod seconds"
echo "  RAMPUP_PERIOD: $rampUpPeriod seconds"
echo "  MEASUREMENT_PERIOD: $measurementPeriod seconds"
echo "  FILTER_RESULTS: $filterResults"
echo
echo "Stress Test Parameters:"
echo "  STRESS_TEST_ALGORITHM: $algorithm"
echo "  STRESS_TEST_MAX_ITERATIONS: $maxIterations"
echo "  STRESS_TEST_PROVISIONING: $provisioning"
if $provisioning; then 
    echo "  STRESS_TEST_PROVISIONING_GENERATE_DATASET: $generateDataset (mvn -P $datasetProfile)"; 
    echo "  STRESS_TEST_PROVISIONING_PARAMETERS: $provisioningParameters"; 
fi

usersPerSecMax=0

case "${algorithm}" in

    incremental)

        echo "  STRESS_TEST_UPS_FIRST: $usersPerSec0 users per second"
        echo "  STRESS_TEST_UPS_INCREMENT: $incrementFactor users per second"

        for (( i=0; i < $maxIterations; i++)); do

            usersPerSec=`echo "$usersPerSec0 + $i * $incrementFactor" | bc`

            echo
            echo "ITERATION: $(( i+1 )) / $maxIterations    Load: $usersPerSec users per second"
            echo 

            runTest $@

            if [[ $testResult == 0 ]]; then 
                usersPerSecMax=$usersPerSec
            else
                echo "INFO: Last iteration failed. Stopping the loop."
                break
            fi

        done

    ;;

    bisection)

        echo "  STRESS_TEST_UPS_LOWER_BOUND: $lowerBound users per second"
        echo "  STRESS_TEST_UPS_UPPER_BOUND: $upperBound users per second"
        echo "  STRESS_TEST_UPS_RESOLUTION: $resolution users per second"

        for (( i=0; i < $maxIterations; i++)); do

            intervalSize=`echo "$upperBound - $lowerBound" | bc`
            usersPerSec=`echo "$lowerBound + $intervalSize * 0.5" | bc`

            echo
            echo "ITERATION: $(( i+1 )) / $maxIterations    Bisection interval: [$lowerBound, $upperBound], intervalSize: $intervalSize, resolution: $resolution,   Load: $usersPerSec users per second"
            echo 

            if [[ `echo "$intervalSize < $resolution" | bc`  == 1 ]]; then echo "INFO: intervalSize < resolution. Stopping the loop."; break; fi
            if [[ `echo "$intervalSize < 0" | bc`           == 1 ]]; then echo "ERROR: Invalid state: lowerBound > upperBound. Stopping the loop."; exit 1; fi

            runTest $@

            if [[ $testResult == 0 ]]; then 
                usersPerSecMax=$usersPerSec
                echo "INFO: Last iteration succeeded. Continuing with the upper half of the interval."
                lowerBound=$usersPerSec
            else
                echo "INFO: Last iteration failed. Continuing with the lower half of the interval."
                upperBound=$usersPerSec
            fi

        done

    ;;

    *) 
        echo "Algorithm '${algorithm}' not supported."
        exit 1
    ;;

esac

echo "Maximal achieved load with passing test: $usersPerSecMax users per second"

if ! $dryRun; then # Generate a Jenkins Plot Plugin-compatible data file
    mkdir -p "$keycloakProjectHome/testsuite/performance/tests/target"
    echo "YVALUE=$usersPerSecMax" > "$keycloakProjectHome/testsuite/performance/tests/target/stress-test-result.properties"
fi
