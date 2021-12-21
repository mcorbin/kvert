# ymlgen: a powerful tool to generate YAML files

`ymlgen` lets you generate yaml files in a declarative way. You can for example use it to manage your Kubernetes manifests.

It leverages the [EDN](https://github.com/edn-format/edn) format and the [Aero](https://github.com/juxt/aero) to do so.

Why use ymlgen ? It's simple (< 170 LoC including namespaces declarations, line breaks...), powerful and extensible.

## Install

For Linux (amd64), download the `ymlgen` binary and put it in your PATH. This binary is built using [GraalVM](https://www.graalvm.org/) so more targets may be added soon.

You can alternatively download the `jar` file and then run it with `java -jar ymlgen.jar` (Java 17 needed).

## Quick start

### Simple EDN example

Once `ymlgen` installed, you are ready to use it. Let's for example generate a yaml file from a simple EDN definition. Put in `pod.edn` this content:

```clojure
{:apiVersion "v1"
 :kind "Pod"
 :metadata {:name "dnsutils"
            :namespace "default"}
 :spec {:containers [{:name "dnsutils"
                     :image "k8s.gcr.io/e2e-test-images/jessie-dnsutils:1.3"
                     :command ["sleep" "3600"]
                     :imagePullPolicy "ifNotPresent"}]
        :restartPolicy "Always"}}
```

Now run `ymlgen yaml --template pod.edn`:

```yaml
---
apiVersion: v1
kind: Pod
metadata:
  name: dnsutils
  namespace: default
spec:
  containers:
  - name: dnsutils
    image: k8s.gcr.io/e2e-test-images/jessie-dnsutils:1.3
    command:
    - sleep
    - '3600'
    imagePullPolicy: ifNotPresent
  restartPolicy: Always
```

You can pass the `-o` (or `--output`) flag to save the output into a file.

As you can see, we can easily translate EDN to YAML. You can define multiple YAML resources into the same file as well:

```clojure
[{:name "yaml-file-1"}
 {:name "yaml-file-2"}]
```

`ymlgen` will output:

```yaml
---
name: yaml-file-1
---
name: yaml-file-2
```

### Customisations

EDN supports `readers`, which can be used to extend it. Let's now put in `pod.edn` this content:

```clojure
{:apiVersion "v1"
 :kind "Pod"
 :metadata {:name "dnsutils"
            :namespace #ymlgen/var :namespace}
 :spec {:containers [{:name "dnsutils"
                      :image #join ["k8s.gcr.io/e2e-test-images/jessie-dnsutils:" #ymlgen/var :container-version]
                      :command ["sleep" #or [#env SLEEP_DURATION 3600]]
                      :imagePullPolicy #profile {:production "ifNotPresent"
                                                 :default "Always"}}]
        :restartPolicy "Always"}}
```

As you an see, we use a few readers (which start with `#`) in this file:

- `#ymlgen/var` which will replace the next keyword (`:namespace` for example here) with a variable value
- `#join` which will concatenate several values together
- `#or` which allows you to define default values
- `#env` to read values from environment variables
- `#profile` which create a switch based on the value on the profile you used to run `ymlgen` (more on that later). This reader also supports default values in `:default`

Readers can be combined together, like in `["sleep" #or [#env SLEEP_DURATION 3600]]` in this example which will first read the SLEEP_DURATION environment variable and fallback to `3600` if it's not defined.

Let's create a new file named `config.edn`:

```clojure
{:variables {:namespace "default"
             :container-version "1.3"}
 :profile :production}
```

This file defines variables (referenced by `#ymlgen/var` in the `pod.edn` file) and the profile (`:production`).

Launch `ymlgen yaml --template pod.edn -c config.edn`, the output is:

```yaml
---
apiVersion: v1
kind: Pod
metadata:
  name: dnsutils
  namespace: default
spec:
  containers:
  - name: dnsutils
    image: k8s.gcr.io/e2e-test-images/jessie-dnsutils:1.3
    command:
    - sleep
    - 3600
    imagePullPolicy: ifNotPresent
  restartPolicy: Always
```

Thank to the readers we are able to customise our manifest. We could for example use variables and the profile to have variation between environment, kubernetes clusters...

## Profile

We configured in this example the profile into the `config.edn` file. You can also set it by configuring the `PROFILE` environment variable when running `ymlgen`.

## More readers

[Aero](https://github.com/juxt/aero), the library used by `ymlgen` to parse EDN files supports tons of readers out of the box. You can find them in the library [documentation](https://github.com/juxt/aero#tag-literals).

For example, the `#include` reader allows you to include an EDN file into another one:

```clojure
{:apiVersion "v1"
 :kind "Pod"
 :metadata {:labels #include "labels.edn"}}
```

`labels.edn` (which can also contain readers if you need to) being:

```clojure
{:foo "bar"
 :environment "prod"}
```

The output if `ymlgen` will be

```yaml
---
apiVersion: v1
kind: Pod
metadata:
  labels:
    foo: bar
    environment: prod
```

Another cool one is `#ref`:

```clojure
{:apiVersion "v1"
 :kind "Pod"
 :metadata {:name "foo"
            :labels {:name #ref [:metadata :name]}}}
```

This content will produce:

```yaml
---
apiVersion: v1
kind: Pod
metadata:
  name: foo
  labels:
    name: foo
```

As you can see, ref allows you to reference another part of your edn file.

Don't hesitate to check the Aero [documentation](https://github.com/juxt/aero#tag-literals) for more examples !
