If you want to see logging of client registrations, heartbeats and discoveries.

== Start the Snoop Service

```
docker run -it -p 8081:8080 ivargrimstad/snoop-service
```

== Set log levels

```
docker ps
./loglevels.sh [insert docker instance id here]
```
