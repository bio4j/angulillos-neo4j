package com.bio4j.angulillos.neo4j;

import java.util.stream.Stream;
import java.util.Iterator;
import java.util.Optional;

import com.bio4j.angulillos.*;
import static com.bio4j.angulillos.conversions.*;

// Neo4j
import org.neo4j.graphdb.*;
import static org.neo4j.graphdb.Direction.*;

public final class Neo4jUntypedGraph
implements
  UntypedGraph<Node, Relationship>
{
  private final GraphDatabaseService neo4jGraph;
  public  final GraphDatabaseService neo4jGraph() { return this.neo4jGraph; }

  public Neo4jUntypedGraph(GraphDatabaseService neo4jGraph) { this.neo4jGraph = neo4jGraph; }

  // TODO drop all these methods after fixing inheritance in angulillos
  @Override
  public void commit() { throw new UnsupportedOperationException(); }

  public void beginTx() { neo4jGraph.beginTx(); }

  @Override
  public void shutdown() { neo4jGraph().shutdown(); }

  @Override
  public void rollback() { throw new UnsupportedOperationException(); }

  /* Convert an `AnyEdgeType` to Neo4j RelationshipType */
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

  /*
    ### *out* methods

  */

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

  /*
    ### *in* methods

  */
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
