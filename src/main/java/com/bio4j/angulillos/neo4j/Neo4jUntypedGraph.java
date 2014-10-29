package com.bio4j.angulillos.neo4j;

import java.util.stream.Stream;
import java.util.Iterator;
import java.util.Optional;

import com.bio4j.angulillos.*;
import static com.bio4j.angulillos.conversions.*;

// Neo4j
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.neo4j.graphdb.Direction.*;

import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintCreator;

public interface Neo4jUntypedGraph extends UntypedGraph <
  // vertex and vertex type
  org.neo4j.graphdb.Node, org.neo4j.graphdb.Label,
  // edge and edge type
  org.neo4j.graphdb.Relationship, org.neo4j.graphdb.RelationshipType
> 
{

  org.neo4j.graphdb.GraphDatabaseService neo4jGraph();

  default void commit() {  }

  default void shutdown() { neo4jGraph().shutdown(); }

  @Override
  default Relationship addEdge(Node from, RelationshipType edgeType, Node to) {

    try ( Transaction tx = neo4jGraph().beginTx() ) {

      Relationship rel = from.createRelationshipTo(to, edgeType); 
      tx.success(); 

      return rel;
    }
  }

  @Override
  default Node addVertex(Label type) {  

    try ( Transaction tx = neo4jGraph().beginTx() ) {

      Node node = neo4jGraph().createNode(type); 
      tx.success(); 

      return node;
    }
  }

  @Override
  default <V> V getPropertyV(Node vertex, String property) {  

    @SuppressWarnings("unchecked")
    V value = (V) vertex.getProperty( property ); 
    return value;
  }

  @Override
  default <V> void setPropertyV(Node vertex, String property, V value) {

    try ( Transaction tx = neo4jGraph().beginTx() ) {

      vertex.setProperty( property, value ); 
      tx.success(); 
    }
  }

  @Override
  default <V> V getPropertyE(Relationship edge, String property) {

    @SuppressWarnings("unchecked")
    V value = (V) edge.getProperty( property ); 
    return value;
  }

  @Override
  default <V> void setPropertyE(Relationship edge, String property, V value) {

    try ( Transaction tx = neo4jGraph().beginTx() ) {

      edge.setProperty( property, value );
      tx.success(); 
    }
    
  }

  @Override
  default Node source(Relationship edge) {

    return edge.getStartNode();
  }

  @Override
  default Node target(Relationship edge) {

    return edge.getEndNode();    
  }

  @Override
  default Optional<Stream<Relationship>> out(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable( stream( vertex.getRelationships( edgeType, OUTGOING ) ) );
  }

  @Override
  default Optional<Stream<Node>> outV(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable ( 
      stream ( 
        vertex.getRelationships( edgeType, OUTGOING ) 
      )
      .map( Relationship::getEndNode ) 
    );
  }

  @Override
  default Optional<Stream<Relationship>> in(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable( stream( vertex.getRelationships( edgeType, INCOMING ) ) );
  }

  @Override
  default Optional<Stream<Node>> inV(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable ( 
      stream ( 
        vertex.getRelationships( edgeType, INCOMING ) 
      )
      .map( Relationship::getStartNode ) 
    );
  }

  default <
    N extends TypedVertex<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    NT extends TypedVertex.Type<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    P extends Property<N,NT,P,V,G,I,Node,Label,Relationship,RelationshipType>, V,
    G extends TypedGraph<G,I,Node,Label,Relationship,RelationshipType>,
    I extends Neo4jUntypedGraph
  > ConstraintCreator uniqueConstraintFor(P property) {

    return neo4jGraph().schema().constraintFor(property.elementType().raw())
      .assertPropertyIsUnique(property.name());
  }

  default <
    N extends TypedVertex<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    NT extends TypedVertex.Type<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    P extends Property<N,NT,P,V,G,I,Node,Label,Relationship,RelationshipType>, V,
    G extends TypedGraph<G,I,Node,Label,Relationship,RelationshipType>,
    I extends Neo4jUntypedGraph
  > ConstraintDefinition createOrGetUniqueConstraintFor(P property) {

    return Optional.ofNullable ( 
      neo4jGraph().schema()
      .getConstraints( property.elementType().raw() )
      .iterator().next()
    ).orElseGet(
      () -> uniqueConstraintFor(property).create()
    );
  }

}