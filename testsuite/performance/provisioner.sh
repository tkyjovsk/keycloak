export PROVISIONER="${PROVISIONER:-docker-compose}"
echo "PROVISIONER=${PROVISIONER}"

cd "$(dirname "$0")"

case "$PROVISIONER" in

    docker-compose)
        if ! docker-compose/provisioner.sh ; then
            exit 1;
        fi
    ;;

    *)
        echo "Unknown provisioner."
        exit 1
    ;;

esac

./healthcheck.sh
