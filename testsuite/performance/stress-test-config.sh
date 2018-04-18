#!/bin/bash

# common settings
export algorithm=incremental
export provisioning=false
export maxIterations=10

export dataset=100u2c
export warmUpPeriod=0

# incremental 
export usersPerSec0=5
export incrementFactor=5

# bisection
export lowPoint=20.000
export highPoint=50.000
export tolerance=1.000

# other
export debug=false

export deleteUsers=true
export deleteUsersWorkers=10
