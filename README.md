# Fake Proxy

**Fake Proxy** is a lightweight Java-based HTTP/HTTPS interception proxy designed for environments where GUI tools like Charles Proxy or Fiddler can’t be installed — for example, restricted servers or headless SSH sessions.

---

## 📑 Table of Contents
- [💡 Overview](#-overview)
- [⚙️ Example Usage](#-example-usage)
- [🏗️ Building the ZIP Package](#-building-the-zip-package)
- [🚀 Running the Application](#-running-the-application)
- [🧩 Command-Line Parameters](#-command-line-parameters)
- [⚙️ Configuration](#-configuration)
- [📋 Configuration Priority](#-configuration-priority)
- [🧩 License](#-license)

---

## 💡 Overview

**Fake Proxy** acts as a lightweight **HTTP/HTTPS interception proxy**, designed to help you inspect, log, and modify real network traffic in environments where installing GUI tools like Charles or Fiddler isn’t possible.

It enables:
- Logging of HTTP and HTTPS requests and responses
- Selective inspection based on host or URL patterns
- Selective header logging — only the headers you care about are shown
- Dynamic modification of requests (URL, headers, body)

It’s particularly useful in restricted or headless environments where you:
- Have only SSH or command-line access
- Cannot install additional debugging tools due to security policies
- Need to analyze real API requests during testing or certification

A typical use case looks like this:
- You run a client locally (for example, `trace-tester`) that sends API requests to your deployed service, e.g., **DataBridge**, running in an upper environment (UAT, staging, or pre-production).
- That service, **DataBridge**, communicates with a third-party API. You want to observe the real outgoing request (for trace, demo, or invalid data emulation).
- To achieve this:
    - Configure **DataBridge** in the upper environment to use a proxy at `localhost:3128` (or another port).
    - Establish a reverse SSH tunnel from your local machine to that environment:

      ```bash
      ssh -o ExitOnForwardFailure=yes -R 3128:localhost:8888 {user-name}@{env-host}
      ```  

    - If **DataBridge** already uses an upstream proxy, you can define it inside Fake Proxy’s configuration.
    - Once the tunnel is active, execute the request from your local tester. The call will flow through the upper environment → reverse tunnel → Fake Proxy → third-party API.
    - You can then see and analyze the full outgoing request — including headers, payloads, and response traces — directly in your terminal.

---

## ⚙️ Example Usage

You can test the proxy locally using a simple `curl` command:

```bash
curl --cacert ./littleproxy-mitm.pem \
     -x http://localhost:8888 \
     -X POST "https://jsonplaceholder.typicode.com/posts" \
     -H "Content-Type: application/json" \
     -d '{ "title": "Proxy test", "body": "Checking proxy","userId": 1 }'
```

The proxy will log the full request and response in the terminal, without requiring any graphical interface.

* Certificates for MITM (Man-In-The-Middle) interception are generated automatically when the application starts (`littleproxy-mitm.pem`, `littleproxy-mitm.p12`).
* **TIP:** Use `--port 0` to bind the proxy to a random available port — it will print the chosen port in the console.

---
## 🏗️ Building the ZIP Package

To build the application and create the deployable ZIP package, run:

```bash
./mvnw clean package
```

After the build completes, the `target/` directory will contain:

```
target/
├── fake-proxy-1.0.jar         # main application file
├── lib/                       # all dependencies
└── fake-proxy-1.0-dist.zip    # ready-to-use distribution
```

---

## 🚀 Running the Application

### 1️⃣ Copy and Unzip the Package

Copy the generated ZIP file to a folder where you plan to run the app (not inside `target/`), for example:

```bash
cp target/fake-proxy-1.0-dist.zip ~/fake-proxy/
cd ~/fake-proxy
unzip fake-proxy-1.0-dist.zip
```

This creates a folder named `fake-proxy-1.0-dist` containing everything needed to start the proxy.

### 2️⃣ Start the Application

Go into the extracted folder and run:

```bash
cd fake-proxy-1.0
java -jar fake-proxy-1.0.jar
```

The proxy will start using the default configuration and listen on port **8888**.

---

## 🧩 Command-Line Parameters

The application accepts the following command-line options:

| Parameter       | Description                                                   | Example                                            |
|-----------------|---------------------------------------------------------------|----------------------------------------------------|
| `--port`        | Listening port (use `0` for random free port)                 | `--port 0`                                         |
| `--proxyHost`   | Upstream proxy host                                           | `--proxyHost corp-proxy.local`                     |
| `--proxyPort`   | Upstream proxy port                                           | `--proxyPort 8080`                                 |
| `--yaml`        | Path to external YAML config file                             | `--yaml my-config.yaml`                            |
| `--urlPatterns` | One or more URL patterns to filter (can repeat)               | `--urlPatterns "^example\.com$"`                   |
| `--logHeader`   | Headers to be logged (can repeat)                             | `--logHeader Content-Type --logHeader Accept`      |
| `--url`         | Rewrite request path and query (relative, without host)       | `--url /api/v2/test?id=1`                          |
| `--setHeader`   | Header update: `name[=value]` (repeatable; no value → delete) | `--setHeader X-Test=123 --setHeader Authorization` |
| `--body`        | Override request body (raw data or `file://<path>`)           | `--body "{ \"foo\": \"bar\" }"`                    |

---

### 🧠 Example: Random Port

Start proxy on a random free port:

```bash
java -jar fake-proxy-1.0.jar --port 0
```

Output:

```
Proxy started on random port: 52917
```

### 🧠 Example: Run with URL Filtering and Header Selection

Run command:

```bash
java -jar fake-proxy-1.0.jar \
      --urlPatterns "^jsonplaceholder\.typicode\.com(:443)?$" \
      --logHeader Content-Type \
      --logHeader Accept
```

Test request:

```bash
curl --cacert ~/fake-proxy/fake-proxy-1.0/littleproxy-mitm.pem \
     -x http://localhost:8888 \
     -X POST "https://jsonplaceholder.typicode.com/posts" \
     -H "Content-Type: application/json" \
     -d '{ "title": "Proxy test", "body": "Checking proxy","userId": 1 }'
```

Expected Fake Proxy output:

```
Proxy started on port 8888

>>> CONNECT jsonplaceholder.typicode.com:443

>>> POST jsonplaceholder.typicode.com/posts
    Accept: */*
    Content-Type: application/json

{ "title": "Proxy test", "body": "Checking proxy","userId": 1 }

<<< Response Status: 201 Created
    Content-Type: application/json; charset=utf-8

{
  "title": "Proxy test",
  "body": "Checking proxy",
  "userId": 1,
  "id": 101
}
```

---

### 🧠 Example: Modifying Request Data

Fake Proxy can rewrite URL, headers, or body before forwarding the request.

Example CLI:

```bash
java -jar fake-proxy-1.0.jar \
     --url /posts \
     --setHeader "User-Agent=FakeProxyTest/1.0" \
     --body "{ \"title\": \"Proxy test\", \"body\": \"Checking proxy\", \"userId\": 42 }"
```

Run a test via `curl`:

```bash
curl --cacert ~/fake-proxy/fake-proxy-1.0/littleproxy-mitm.pem \
     -x http://localhost:8888 \
     -X POST "https://jsonplaceholder.typicode.com/posts" \
     -H "Content-Type: application/json" \
     -d '{ "title": "Proxy test", "body": "Checking proxy","userId": 7 }'
```

**Server response:**
```json
{
  "title": "Proxy test",
  "body": "Checking proxy",
  "userId": 42,
  "id": 101
}
```

---

## ⚙️ Configuration

You can configure the application either via command-line arguments or using an external YAML file.

### Default Built-In Configuration

The JAR already contains a minimal internal configuration:

```yaml
listeningPort: 8888
```

### External YAML Configuration Example

You can create your own configuration file, for example `my-config.yaml`:

```yaml
listeningPort: 8888

proxy:
  host: some-proxy.global
  port: 8080

filter:
  urlPatterns:
    - "^httpbin\.org(:443)?$"
    - "^jsonplaceholder\.typicode\.com(:443)?$"
  headers:
    - Content-Type
    - Content-Length
    - Accept
```

Run the application with your configuration:

```bash
java -jar fake-proxy-1.0.jar --yaml my-config.yaml
```

---

## 📋 Configuration Priority

| Priority | Source                                         | Description                     |
|----------|------------------------------------------------|---------------------------------|
| 🥇 1     | **Command-line arguments**                     | Always override everything else |
| 🥈 2     | **YAML file provided with `--yaml`**           | Optional configuration file     |
| 🥉 3     | **Built-in `application.yaml` inside the JAR** | Default minimal setup           |

If a parameter appears in multiple places, the value from the higher-priority source is used.


---

## 🧩 License

MIT
