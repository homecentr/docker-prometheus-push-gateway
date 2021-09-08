FROM prom/pushgateway:v1.2.0 as original

FROM homecentr/base:3.4.0-alpine

ENV PUSHGATEWAY_ARGS=""

COPY --from=original /bin/pushgateway /bin/pushgateway

COPY ./fs/ /

RUN mkdir /pushgateway && \
    chmod a+x /bin/pushgateway

EXPOSE 9091