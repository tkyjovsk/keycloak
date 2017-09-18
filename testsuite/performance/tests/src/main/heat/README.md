# HEAT How-to

## Create Stack

`openstack stack create -t cross-dc.yml -e cross-dc-parameters.yml keycloak-cross-dc`

## Delete Stack

`openstack stack delete --yes keycloak-cross-dc`

## Get Output

### Service IPs

```
KEYS=$(openstack stack output list keycloak-cross-dc -c output_key -f value)
for K in $KEYS; do V=`openstack stack output show keycloak-cross-dc $K -c output_value -f value`; echo $K=$V; done
```

