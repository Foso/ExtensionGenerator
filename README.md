<h1 align="center">Extension Generator </h1>

[![jCenter](https://img.shields.io/badge/Apache-2.0-green.svg
)](https://github.com/Foso/ExtensionGenerator/blob/master/LICENSE)
  

> Annotation Processor that generate extension converter functions to annotated classes.
The converter functions will map identical field name from source class to target class

## Usage

> :information_source: This is an early alpha version

Add the @Extension annotation above the class/interface, that should be extended.

Example:
This:

```java
@Extension(to = [SimpleUser::class, OtherUser::class])
interface User {
    val name: String
    val email: String
    val street: String
    val age: Int
}


data class SimpleUser(
        val name: String,
        val age: Int
)

data class OtherUser(
        val name: String,
        val street: String
)
```

will generate this to the build folder:

```java
// //Generated
package de.jensklingenberg

fun User.toSimpleUser(): SimpleUser {
    val simpleuser = SimpleUser(
    name = name,
    age = age
    )
    return simpleuser
}

fun User.toOtherUser(): OtherUser {
    val otheruser = OtherUser(
    name = name,
    street = street
    )
    return otheruser
}

```

### 👷 Project Structure
 * <kbd>app</kbd> - A Kotlin JVM project which uses the annotation processor
 * <kbd>processor</kbd> - The source project of the annotation processor

## 📜 License

-------

This project is licensed under Apache License, Version 2.0

    Copyright 2019 Jens Klingenberg

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
