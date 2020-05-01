[![Project status](https://img.shields.io/badge/Project%20status-stable%20%26%20actively%20maintaned-green.svg)](https://github.com/homecentr/docker-portainer/graphs/commit-activity) 
[![](https://img.shields.io/github/issues-raw/homecentr/docker-portainer/bug?label=open%20bugs)](https://github.com/homecentr/docker-portainer/labels/bug) 
[![](https://images.microbadger.com/badges/version/homecentr/cadvisor.svg)](https://hub.docker.com/repository/docker/homecentr/cadvisor)
[![](https://img.shields.io/docker/pulls/homecentr/cadvisor.svg)](https://hub.docker.com/repository/docker/homecentr/cadvisor) 
[![](https://img.shields.io/docker/image-size/homecentr/cadvisor/latest)](https://hub.docker.com/repository/docker/homecentr/cadvisor)

![CI/CD on master](https://github.com/homecentr/docker-portainer/workflows/CI/CD%20on%20master/badge.svg)
![Regular Docker image vulnerability scan](https://github.com/homecentr/docker-portainer/workflows/Regular%20Docker%20image%20vulnerability%20scan/badge.svg)


# HomeCentr - cAdvisor
This docker image is a repack of the original [Portainer](https://www.portainer.io/) compliant with the HomeCenter docker images standard (S6 overlay, privilege drop etc.).

## Usage

```yml
version: "3.7"
services:
  portainer:
    build: .
    image: homecentr/portainer
    restart: unless-stopped
    environment:
      PORTAINER_ARGS: "--no-auth"
      WAIT_FOR_NS_RECORD: "tasks.agent"
      WAIT_FOR_NS_RECORD_TIMEOUT: 30
    healthcheck:
      start_period: 40s    
    ports:
      - "9000:9000/tcp"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

## Environment variables

| Name | Default value | Description |
|------|---------------|-------------|
| PUID | 7077 | UID of the user cadvisor should be running as. The UID must have sufficient rights to read from the Docker socket. |
| PGID | 7077 | GID of the user cadvisor should be running as. You must set the PUID if you want to set the PGID variable. |
| PORTAINER_ARGS | | Command line [arguments](https://portainer.readthedocs.io/en/stable/configuration.html#available-flags) to the Portainer executable. By default the logs are redirected to the container output |
| WAIT_FOR_NS_RECORD | | DNS record which should be successfully resolved before starting the Portainer. This is useful when running the container in Docker Swarm and using the tasks.<service> DNS alias to automatically discover agents. If the DNS record cannot be resolved when the container is starting, the execution fails. |
| WAIT_FOR_NS_RECORD_TIMEOUT | 20 | Timeout of how long the container will try to wait for the DNS record to be successfully resolved. |

> When using the wait for NS record functionality, you **must set the health check by the start period as shown in the example above to approximately 10 seconds + NS record timeout**. This can be done by <service>.healthcheck.start_period property in your compose file or by command line arguments. If you don't do this, the health check may start before the portainer process starts which would cause the health check to fail and therefore infinite restart loop of the container. 

## Exposed ports

| Port | Protocol | Description |
|------|------|-------------|
| 9000 | TCP | Web UI |
| 8000 | TCP | Used for [edge agent](https://www.portainer.io/2019/07/portainer-edge-agent/) functionality only and therefore it is not explicitly exposed in the Docker image. | 

## Volumes

| Container path | Description |
|-------------|-----------------|
| /data | Portainer data |

> Make sure you mount the Docker socket when using outside of Docker swarm.

## Security
The container is regularly scanned for vulnerabilities and updated. Further info can be found in the [Security tab](https://github.com/homecentr/docker-portainer/security).

### Container user
The container supports privilege drop. Even though the container starts as root, it will use the permissions only to perform the initial set up. The cadvisor process runs as UID/GID provided in the PUID and PGID environment variables.

:warning: Do not change the container user directly using the `user` Docker compose property or using the `--user` argument. This would break the privilege drop logic.

:bulb: To grant a user the permission to read Docker socket, you can add them to the docker group which is automatically created as a part of the Docker installation.