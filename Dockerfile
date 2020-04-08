ARG PORTAINER_VERSION=1.23.0

FROM homecentr/base:1.0.0 as base
FROM portainer/portainer:$PORTAINER_VERSION as portainer

FROM alpine:3.11.5

# The argument must be defined in each stage where it should be used.
# It automatically inherits the global (default) value.
ARG PORTAINER_VERSION

RUN echo "Version: ${PORTAINER_VERSION}"

LABEL maintainer="Lukas Holota <me@lholota.com>"
LABEL org.homecentr.dependency-version=$PORTAINER_VERSION

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