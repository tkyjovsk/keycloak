PROVISIONER="${PROVISIONER:-docker-compose}"
echo "PROVISIONER=${PROVISIONER}"

case "$PROVISIONER" in

    docker-compose)
        ./provision-docker-compose.sh
    ;;

    *)
        echo "Unknown provisioner."
        exit 1
    ;;

esac
