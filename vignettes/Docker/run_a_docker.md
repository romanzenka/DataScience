# Run a Docker

## First Build

    docker build .

    docker build -f /path/to/a/Dockerfile .

    docker build -t shykes/myapp . # You can specify a repository and tag at which to save the new image if the build succeed
    
options

    Usage: docker build [OPTIONS] PATH | URL | -

    Build a new image from the source code at PATH

    --build-arg=[]                  Set build-time variables
    --cpu-shares                    CPU Shares (relative weight)
    --cgroup-parent=""              Optional parent cgroup for the container
    --cpu-period=0                  Limit the CPU CFS (Completely Fair Scheduler) period
    --cpu-quota=0                   Limit the CPU CFS (Completely Fair Scheduler) quota
    --cpuset-cpus=""                CPUs in which to allow execution, e.g. `0-3`, `0,1`
    --cpuset-mems=""                MEMs in which to allow execution, e.g. `0-3`, `0,1`
    --disable-content-trust=true    Skip image verification
    -f, --file=""                   Name of the Dockerfile (Default is 'PATH/Dockerfile')
    --force-rm                      Always remove intermediate containers
    --help                          Print usage
    --isolation=""                  Container isolation technology
    --label=[]                      Set metadata for an image
    -m, --memory=""                 Memory limit for all build containers
    --memory-swap=""                A positive integer equal to memory plus swap. Specify -1 to enable unlimited swap.
    --no-cache                      Do not use cache when building the image
    --pull                          Always attempt to pull a newer version of the image
    -q, --quiet                     Suppress the build output and print image ID on success
    --rm=true                       Remove intermediate containers after a successful build
    --shm-size=[]                   Size of `/dev/shm`. The format is `<number><unit>`. `number` must be greater than `0`.  Unit is optional and can be `b` (bytes), `k` (kilobytes), `m` (megabytes), or `g` (gigabytes). If you omit the unit, the system uses bytes. If you omit the size entirely, the system uses `64m`.
    -t, --tag=[]                    Name and optionally a tag in the 'name:tag' format
    --ulimit=[]                     Ulimit options


## Run

    $ docker run [OPTIONS] IMAGE[:TAG|@DIGEST] [COMMAND] [ARG...]

    Usage: docker run [OPTIONS] IMAGE [COMMAND] [ARG...]

    Run a command in a new container

      -a, --attach=[]               Attach to STDIN, STDOUT or STDERR
      --add-host=[]                 Add a custom host-to-IP mapping (host:ip)
      --blkio-weight=0              Block IO weight (relative weight)
      --blkio-weight-device=[]      Block IO weight (relative device weight, format: `DEVICE_NAME:WEIGHT`)
      --cpu-shares=0                CPU shares (relative weight)
      --cap-add=[]                  Add Linux capabilities
      --cap-drop=[]                 Drop Linux capabilities
      --cgroup-parent=""            Optional parent cgroup for the container
      --cidfile=""                  Write the container ID to the file
      --cpu-period=0                Limit CPU CFS (Completely Fair Scheduler) period
      --cpu-quota=0                 Limit CPU CFS (Completely Fair Scheduler) quota
      --cpuset-cpus=""              CPUs in which to allow execution (0-3, 0,1)
      --cpuset-mems=""              Memory nodes (MEMs) in which to allow execution (0-3, 0,1)
      -d, --detach                  Run container in background and print container ID
      --detach-keys                 Specify the escape key sequence used to detach a container
      --device=[]                   Add a host device to the container
      --device-read-bps=[]          Limit read rate (bytes per second) from a device (e.g., --device-read-bps=/dev/sda:1mb)
      --device-read-iops=[]         Limit read rate (IO per second) from a device (e.g., --device-read-iops=/dev/sda:1000)
      --device-write-bps=[]         Limit write rate (bytes per second) to a device (e.g., --device-write-bps=/dev/sda:1mb)
      --device-write-iops=[]        Limit write rate (IO per second) to a device (e.g., --device-write-bps=/dev/sda:1000)
      --disable-content-trust=true  Skip image verification
      --dns=[]                      Set custom DNS servers
      --dns-opt=[]                  Set custom DNS options
      --dns-search=[]               Set custom DNS search domains
      -e, --env=[]                  Set environment variables
      --entrypoint=""               Overwrite the default ENTRYPOINT of the image
      --env-file=[]                 Read in a file of environment variables
      --expose=[]                   Expose a port or a range of ports
      --group-add=[]                Add additional groups to run as
      -h, --hostname=""             Container host name
      --help                        Print usage
      -i, --interactive             Keep STDIN open even if not attached
      --ip=""                       Container IPv4 address (e.g. 172.30.100.104)
      --ip6=""                      Container IPv6 address (e.g. 2001:db8::33)
      --ipc=""                      IPC namespace to use
      --isolation=""                Container isolation technology
      --kernel-memory=""            Kernel memory limit
      -l, --label=[]                Set metadata on the container (e.g., --label=com.example.key=value)
      --label-file=[]               Read in a file of labels (EOL delimited)
      --link=[]                     Add link to another container
      --log-driver=""               Logging driver for container
      --log-opt=[]                  Log driver specific options
      -m, --memory=""               Memory limit
      --mac-address=""              Container MAC address (e.g. 92:d0:c6:0a:29:33)
      --memory-reservation=""       Memory soft limit
      --memory-swap=""              A positive integer equal to memory plus swap. Specify -1 to enable unlimited swap.
      --memory-swappiness=""        Tune a container's memory swappiness behavior. Accepts an integer between 0 and 100.
      --name=""                     Assign a name to the container
      --net="bridge"                Connect a container to a network
                                    'bridge': create a network stack on the default Docker bridge
                                    'none': no networking
                                    'container:<name|id>': reuse another container's network stack
                                    'host': use the Docker host network stack
                                    '<network-name>|<network-id>': connect to a user-defined network
      --net-alias=[]                Add network-scoped alias for the container
      --oom-kill-disable            Whether to disable OOM Killer for the container or not
      --oom-score-adj=0             Tune the host's OOM preferences for containers (accepts -1000 to 1000)
      -P, --publish-all             Publish all exposed ports to random ports
      -p, --publish=[]              Publish a container's port(s) to the host
      --pid=""                      PID namespace to use
      --pids-limit=-1                Tune container pids limit (set -1 for unlimited), kernel >= 4.3
      --privileged                  Give extended privileges to this container
      --read-only                   Mount the container's root filesystem as read only
      --restart="no"                Restart policy (no, on-failure[:max-retry], always, unless-stopped)
      --rm                          Automatically remove the container when it exits
      --shm-size=[]                 Size of `/dev/shm`. The format is `<number><unit>`. `number` must be greater than `0`.  Unit is optional and can be `b` (bytes), `k` (kilobytes), `m` (megabytes), or `g` (gigabytes). If you omit the unit, the system uses bytes. If you omit the size entirely, the system uses `64m`.
      --security-opt=[]             Security Options
      --sig-proxy=true              Proxy received signals to the process
      --stop-signal="SIGTERM"       Signal to stop a container
      -t, --tty                     Allocate a pseudo-TTY
      -u, --user=""                 Username or UID (format: <name|uid>[:<group|gid>])
      --userns=""                   Container user namespace
                                    'host': Use the Docker host user namespace
                                    '': Use the Docker daemon user namespace specified by `--userns-remap` option.
      --ulimit=[]                   Ulimit options
      --uts=""                      UTS namespace to use
      -v, --volume=[host-src:]container-dest[:<options>]
                                    Bind mount a volume. The comma-delimited
                                    `options` are [rw|ro], [z|Z],
                                    [[r]shared|[r]slave|[r]private], and
                                    [nocopy]. The 'host-src' is an absolute path
                                    or a name value.
      --volume-driver=""            Container's volume driver
      --volumes-from=[]             Mount volumes from the specified container(s)
      -w, --workdir=""              Working directory inside the container