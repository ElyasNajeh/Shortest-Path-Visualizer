# Shortest Path Visualizer

A JavaFX desktop application that loads a weighted directed graph and visualizes routes calculated with Dijkstra's algorithm.

## Features

- Load graph data from a text file.
- Choose source and destination vertices.
- Calculate shortest distance, least travel time, or compare both routes.
- Visualize the full graph with directed route arrows, labeled endpoints, and distinct path colors.
- Display total cost and the ordered vertex path.

## Technologies & Tools

- **Java 25** — application and algorithm implementation.
- **JavaFX 25** — desktop interface and canvas-based graph visualization.
- **Maven Wrapper** — portable dependency management, testing, building, and launching.
- **JUnit 5** — algorithm, parser, heap, and sample-data tests.

## Data Structures

- **`LinkedList` / `Node`** — custom adjacency lists storing each vertex's outgoing edges.
- **`MyArrayList`** — custom resizable sorted array used for unique vertex names and binary lookup.
- **`Heap`** — custom indexed min-heap used as Dijkstra's priority queue.
- **Arrays** — distances, parents, visited state, and reconstructed paths.

## Algorithm

Dijkstra's algorithm runs on the adjacency-list graph using either edge distance or travel time as its weight. The custom min-heap selects the next lowest-cost vertex, while parent links reconstruct the highlighted route.

## Prerequisites

- JDK 25 with `JAVA_HOME` configured.

## Getting Started

```bash
git clone <repository-url>
cd FxGraphProject
./mvnw clean
./mvnw javafx:run
```

On Windows, replace `./mvnw` with `.\mvnw.cmd`.

## Project Structure

```text
src/main/java/application/       Application, algorithm, and custom data structures
src/main/resources/application/  JavaFX stylesheet
sample-data/                     Ready-to-load graph input
pom.xml                          Maven build and JavaFX configuration
```
