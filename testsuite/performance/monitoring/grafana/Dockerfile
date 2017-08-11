FROM grafana/grafana:4.4.3
ENV GF_DASHBOARDS_JSON_ENABLED true
ENV GF_DASHBOARDS_JSON_PATH /etc/grafana/dashboards/
COPY dashboard.json /etc/grafana/dashboards/
ADD entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
