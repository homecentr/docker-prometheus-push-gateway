FROM prom/pushgateway:v1.4.1 as original

FROM homecentr/base:2.4.3-alpine

ENV PUSHGATEWAY_ARGS=""

COPY --from=original /bin/pushgateway /bin/pushgateway

COPY ./fs/ /

RUN mkdir /pushgateway && \
    chmod a+x /bin/pushgateway

EXPOSE 9091