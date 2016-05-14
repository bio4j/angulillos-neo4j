
```java
package com.bio4j.angulillos.neo4j;

import java.util.stream.Stream;
import java.util.Iterator;
import java.util.Optional;

import com.bio4j.angulillos.*;
import static com.bio4j.angulillos.conversions.*;

// Neo4j
import org.neo4j.graphdb.*;
import static org.neo4j.graphdb.Direction.*;
```


## Neo4j untyped graph implementation

### Normal use

You would

1. open a transaction `tx = g.beginTx()`
2. do stuff
3. if everything looks OK, `tx.success()`
4. at last commit `tx.commit()`

The proper style for this would be

``` java
try( tx = g.beginTx() ) {

  // create nodes, whatever
  tx.success();
}
```


```java
public final class Neo4jUntypedGraph
implements
  UntypedGraph.Transactional<Node, Relationship>
{
  private final GraphDatabaseService neo4jGraph;
  public  final GraphDatabaseService neo4jGraph() { return this.neo4jGraph; }

  public Neo4jUntypedGraph(GraphDatabaseService neo4jGraph) { this.neo4jGraph = neo4jGraph; }

  public final class Tx implements AutoCloseable, UntypedGraph.Transaction<Node, Relationship> {

    private final org.neo4j.graphdb.Transaction tx;
    public Tx(org.neo4j.graphdb.Transaction tx) { this.tx = tx; }

    @Override
    public final Neo4jUntypedGraph graph() { return Neo4jUntypedGraph.this; }
```

Commit here will either properly try to commit the transaction if it has *only* been marked by `success()`, or `rollback()` it in any other case.

```java
    @Override
    public final void commit() { tx.close(); }
```

This method will inconditionally rollback the wrapped transaction.

```java
    @Override
    public final void rollback() { tx.terminate(); }
```

Note that for a transaction to be actually committed you need to call `success()` on it before.

```java
    public final void success() { tx.success(); }

    @Override
    public final void close() { commit(); }
  }

  @Override
  public final Tx beginTx() { return new Tx( neo4jGraph.beginTx() ); }

  @Override
  public final void shutdown() { neo4jGraph().shutdown(); }
```

Convert an `AnyEdgeType` to Neo4j RelationshipType

```java
  private static RelationshipType Neo4jRelType(AnyEdgeType edgeType) {

    return DynamicRelationshipType.withName(edgeType._label());
  }

  private static Label Neo4jLabel(AnyVertexType vertexType) {

    return DynamicLabel.label( vertexType._label() );
  }

  @Override
  public Relationship addEdge(Node from, AnyEdgeType edgeType, Node to) {

    return from.createRelationshipTo(to, Neo4jRelType(edgeType));
  }

  @Override
  public Node addVertex(AnyVertexType vertexType) {

    return neo4jGraph.createNode(Neo4jLabel(vertexType));
  }


  //////////////////////////////////////////////////////////////////////////////////

  @Override
  public <V> V getPropertyV(Node vertex, AnyProperty property) {

    @SuppressWarnings("unchecked")
    V value = (V) vertex.getProperty(property._label());
    return value;
  }

  @Override
  public <V> Node setPropertyV(Node vertex, AnyProperty property, V value) {

    vertex.setProperty(property._label(), value);
    return vertex;
  }

  @Override
  public <V> V getPropertyE(Relationship edge, AnyProperty property) {

    @SuppressWarnings("unchecked")
    V value = (V) edge.getProperty(property._label());
    return value;
  }

  @Override
  public <V> Relationship setPropertyE(Relationship edge, AnyProperty property, V value) {

    edge.setProperty(property._label(), value);
    return edge;
  }

  @Override
  public Node source(Relationship edge) {

    return edge.getStartNode();
  }

  @Override
  public Node target(Relationship edge) {

    return edge.getEndNode();
  }
```


### *out* methods



```java
  @Override
  public Stream<Relationship> outE(Node vertex, AnyEdgeType edgeType) {

    return stream( vertex.getRelationships(Neo4jRelType(edgeType), OUTGOING) );
  }

  @Override
  public Relationship outOneE(Node vertex, AnyEdgeType edgeType) {

    return vertex.getSingleRelationship(Neo4jRelType(edgeType), OUTGOING);
  }

  @Override
  public Optional<Relationship> outAtMostOneE(Node vertex, AnyEdgeType edgeType) {

    return Optional.ofNullable(
      outOneE(vertex, edgeType)
    );
  }

  @Override
  public Stream<Node> outV(Node vertex, AnyEdgeType edgeType) {

    return stream(
      vertex.getRelationships(Neo4jRelType(edgeType), OUTGOING)
    ).map( Relationship::getEndNode );
  }

  @Override
  public Node outOneV(Node vertex, AnyEdgeType edgeType) {

    return outOneE(vertex, edgeType).getEndNode();
  }

  @Override
  public Optional<Node> outAtMostOneV(Node vertex, AnyEdgeType edgeType) {

    return outAtMostOneE(vertex, edgeType).map(Relationship::getEndNode);
  }
```


### *in* methods



```java
  @Override
  public Stream<Relationship> inE(Node vertex, AnyEdgeType edgeType) {

    return stream( vertex.getRelationships(Neo4jRelType(edgeType), INCOMING) );
  }

  @Override
  public Relationship inOneE(Node vertex, AnyEdgeType edgeType) {

    return vertex.getSingleRelationship(Neo4jRelType(edgeType), INCOMING);
  }

  @Override
  public Optional<Relationship> inAtMostOneE(Node vertex, AnyEdgeType edgeType) {

    return Optional.ofNullable(
      inOneE(vertex, edgeType)
    );
  }

  @Override
  public Stream<Node> inV(Node vertex, AnyEdgeType edgeType) {

    return stream (
      vertex.getRelationships(Neo4jRelType(edgeType), INCOMING)
    ).map( Relationship::getStartNode );
  }

  @Override
  public Node inOneV(Node vertex, AnyEdgeType edgeType) {

    return inOneE(vertex, edgeType).getStartNode();
  }

  @Override
  public Optional<Node> inAtMostOneV(Node vertex, AnyEdgeType edgeType) {

    return inAtMostOneE(vertex, edgeType).map(Relationship::getStartNode);
  }
}

```




[main/java/com/bio4j/angulillos/neo4j/Neo4jUntypedGraph.java]: Neo4jUntypedGraph.java.md