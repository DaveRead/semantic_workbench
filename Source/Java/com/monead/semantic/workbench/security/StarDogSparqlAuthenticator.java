package com.monead.semantic.workbench.security;

import java.net.URI;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.atlas.web.auth.AbstractCredentialsAuthenticator;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;

/**
 * Provide HTTP authentication for StarDog interactions
 *  
 * @author David Read
 *
 */
public class StarDogSparqlAuthenticator extends
    AbstractCredentialsAuthenticator implements HttpAuthenticator {

  /**
   * No operation
   */
  public StarDogSparqlAuthenticator() {

  }

  @Override
  public void apply(AbstractHttpClient arg0, HttpContext arg1, URI arg2) {
    super.apply(arg0, arg1, arg2);
    final HttpParams params = arg0.getParams();
    // SD-Connection-String: Reasoning=DL
    params.setParameter("SD-Connection-String", "Reasoning=DL");
    arg1.setAttribute("SD-Connection-String", "Reasoning=DL");

  }

  @Override
  protected char[] getPassword(URI arg0) {
    // TODO Auto-generated method stub
    return "admin".toCharArray();
  }

  @Override
  protected String getUserName(URI arg0) {
    // TODO Auto-generated method stub
    return "admin";
  }

  @Override
  protected boolean hasPassword(URI arg0) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  protected boolean hasUserName(URI arg0) {
    // TODO Auto-generated method stub
    return true;
  }

}
