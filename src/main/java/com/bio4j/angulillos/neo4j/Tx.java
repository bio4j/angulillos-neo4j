package com.bio4j.angulillos.neo4j;

import com.bio4j.angulillos.UntypedGraph;
import org.neo4j.graphdb.*;

public final class Tx implements AutoCloseable, UntypedGraph.Transaction<Node, Relationship> {

  private final org.neo4j.graphdb.Transaction tx;
  private final Neo4jUntypedGraph graph;

  public Tx(org.neo4j.graphdb.Transaction tx, Neo4jUntypedGraph graph) {

    this.tx     = tx;
    this.graph  = graph;
  }

  @Override
  public final Neo4jUntypedGraph graph() { return graph; }

  /* Commit here will either properly try to commit the transaction if it has *only* been marked by `success()`, or `rollback()` it in any other case. */
  @Override
  public final void commit() { tx.close(); }

  /* This method will inconditionally rollback the wrapped transaction. */
  @Override
  public final void rollback() { tx.terminate(); }

  /* Note that for a transaction to be actually committed you need to call `success()` on it before. */
  public final void success() { tx.success(); }

  @Override
  public final void close() { commit(); }
}
