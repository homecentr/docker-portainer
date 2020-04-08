FROM homecentr/base:1.1.0 as base
FROM portainer/portainer:1.23.2 as portainer

FROM alpine:3.11.5

LABEL maintainer="Lukas Holota <me@lholota.com>"
LABEL io.homecentr.dependency-version=1.23.2

# Additional arguments to portainer binary
ENV PORTAINER_ARGS=""

RUN apk add --no-cache \
      shadow=4.7-r1 \
      curl=7.67.0-r0

COPY --from=base / /
COPY --from=portainer / /
COPY ./fs/ /

HEALTHCHECK --interval=10s --timeout=10s --start-period=5s --retries=3 CMD [ "curl", "http://127.0.0.1:8000" ]

# Web user inteface
EXPOSE 9000

# Portainer configuration
VOLUME [ "/data" ]

ENTRYPOINT ["/init"]