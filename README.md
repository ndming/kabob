# Kabob: Build web blog posts with Kotlin/WASM and Compose Multiplatform

![Fourier Series Visualization](gallery/cover.gif)

[Try it yourself](https://ndming.github.io/visualizations/fourier-series/)

This repository explores the potential of web app development with the current state of Kotlin/WASM and Compose Multiplatform. 
The framework is fitted for building interactive web-based blog posts and visualization articles.

## Build instructions
- Clone the repo with

```
git clone https://github.com/ndming/kabob.git
```

- Open the project in IntelliJ IDEA
- Each web page can be built separately, to build the `Pendulum` simulation page, for example:

```
Gradle Task Panel > pages > Pendulum > Tasks > kotlin browser > wasmJsBrowserDevelopmentRun
```

