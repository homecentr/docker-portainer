FROM portainer/portainer:1.23.2 as portainer

FROM homecentr/base:2.4.3-alpine

LABEL maintainer="Lukas Holota <me@lholota.com>"
LABEL io.homecentr.dependency-version=1.23.2

# Additional arguments to the Portainer binary
ENV PORTAINER_ARGS=""

# Stop the container if the init step fails (i.e. wait for ns)
ENV S6_BEHAVIOUR_IF_STAGE2_FAILS=2

RUN apk add --no-cache \
      shadow=4.7-r1 \
      curl=7.67.0-r0

# Portainer is based on scratch and therefore we can safely copy the whole image fs
COPY --from=portainer / /

# Copy S6 configuration
COPY ./fs/ /

HEALTHCHECK --interval=10s --timeout=5s --start-period=20s --retries=3 CMD curl --fail http://127.0.0.1:9000 || exit 1

# Web user inteface
EXPOSE 9000

# Portainer configuration
VOLUME [ "/data" ]

ENTRYPOINT ["/init"]