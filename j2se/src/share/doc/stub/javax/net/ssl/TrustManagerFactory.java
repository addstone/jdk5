/*
 * @(#)TrustManagerFactory.java	1.10 04/02/16
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */
  
/*
 * NOTE:
 * Because of various external restrictions (i.e. US export
 * regulations, etc.), the actual source code can not be provided
 * at this time. This file represents the skeleton of the source
 * file, so that javadocs of the API can be created.
 */

package javax.net.ssl;

import java.security.*;

import java.security.Security;
import sun.security.jca.GetInstance;

/** 
 * This class acts as a factory for trust managers based on a
 * source of trust material. Each trust manager manages a specific
 * type of trust material for use by secure sockets. The trust
 * material is based on a KeyStore and/or provider specific sources.
 *
 * @since 1.4
 * @see TrustManager
 * @version 1.15, 01/29/04
 */
public class TrustManagerFactory
{

    /** 
     * Creates a TrustManagerFactory object.
     *
     * @param factorySpi the delegate
     * @param provider the provider
     * @param algorithm the algorithm
     */
    protected TrustManagerFactory(TrustManagerFactorySpi factorySpi, Provider
        provider, String algorithm)
    { }

    /** 
     * Obtains the default TrustManagerFactory algorithm name.
     *
     * <p>The default TrustManager can be changed at runtime by setting
     * the value of the "ssl.TrustManagerFactory.algorithm" security
     * property (set in the Java security properties file or by calling
     * {@link java.security.Security#setProperty(String, String) })
     * to the desired algorithm name.
     *
     * @return the default algorithm name as specified in the
     * Java security properties, or an implementation-specific default
     * if no such property exists.
     */
    public static final String getDefaultAlgorithm() {
        return null;
    }

    /** 
     * Returns the algorithm name of this <code>TrustManagerFactory</code>
     * object.
     *
     * <p>This is the same name that was specified in one of the
     * <code>getInstance</code> calls that created this
     * <code>TrustManagerFactory</code> object.
     *
     * @return the algorithm name of this <code>TrustManagerFactory</code>
     *		object
     */
    public final String getAlgorithm() {
        return null;
    }

    /** 
     * Generates a <code>TrustManagerFactory</code> object that implements the
     * specified trust management algorithm.
     * <P>
     * If the default provider package provides an implementation of the
     * requested trust management algorithm, an instance of
     * <code>TrustManagerFactory</code> containing that implementation is
     * returned.  If the algorithm is not available in the default provider
     * package, other provider packages are searched.
     *
     * @param algorithm the standard name of the requested trust management
     *		algorithm.
     * @return the new <code>TrustManagerFactory</code> object
     * @exception NoSuchAlgorithmException if the specified algorithm is not
     *		available in the default provider package or any of the other
     *		provider packages that were searched.
     */
    public static final TrustManagerFactory getInstance(String algorithm)
        throws NoSuchAlgorithmException
    {
        return null;
    }

    /** 
     * Generates a <code>TrustManagerFactory</code> object for the specified
     * trust management algorithm from the specified provider.
     *
     * @param algorithm the standard name of the requested trust management
     *		algorithm.
     * @param provider the name of the provider
     * @return the new <code>TrustManagerFactory</code> object
     * @throws NoSuchAlgorithmException if the specified algorithm is not
     *		available from the specified provider.
     * @throws NoSuchProviderException if the specified provider has not
     *		been configured.
     * @throws IllegalArgumentException if the provider name is null or empty.
     */
    public static final TrustManagerFactory getInstance(String algorithm, String
        provider) throws NoSuchAlgorithmException, NoSuchProviderException
    {
        return null;
    }

    /** 
     * Generates a <code>TrustManagerFactory</code> object for the specified
     * trust management algorithm from the specified provider.
     *
     * @param algorithm the standard name of the requested trust management
     *		algorithm.
     * @param provider an instance of the provider
     * @return the new <code>TrustManagerFactory</code> object
     * @throws NoSuchAlgorithmException if the specified algorithm is not
     *		available from the specified provider.
     * @throws IllegalArgumentException if the provider is null.
     */
    public static final TrustManagerFactory getInstance(String algorithm,
        Provider provider) throws NoSuchAlgorithmException
    {
        return null;
    }

    /** 
     * Returns the provider of this <code>TrustManagerFactory</code> object.
     *
     * @return the provider of this <code>TrustManagerFactory</code> object
     */
    public final Provider getProvider() {
        return null;
    }

    /** 
     * Initializes this factory with a source of certificate
     * authorities and related trust material.
     * <P>
     * The provider typically uses a KeyStore as a basis for making
     * trust decisions.
     * <P>
     * For more flexible initialization, please see
     * {@link #init(ManagerFactoryParameters)}.
     *
     * @param ks the key store, or null
     * @throws KeyStoreException if this operation fails
     */
    public final void init(KeyStore ks) throws KeyStoreException { }

    /** 
     * Initializes this factory with a source of provider-specific
     * trust material.
     * <P>
     * In some cases, initialization parameters other than a keystore
     * may be needed by a provider.  Users of that particular provider
     * are expected to pass an implementation of the appropriate
     * <CODE>ManagerFactoryParameters</CODE> as defined by the
     * provider.  The provider can then call the specified methods in
     * the <CODE>ManagerFactoryParameters</CODE> implementation to obtain the
     * needed information.
     *
     * @param spec an implementation of a provider-specific parameter
     *		specification
     * @throws InvalidAlgorithmParameterException if an error is
     *		encountered
     */
    public final void init(ManagerFactoryParameters spec)
        throws InvalidAlgorithmParameterException
    { }

    /** 
     * Returns one trust manager for each type of trust material.
     *
     * @return the trust managers
     */
    public final TrustManager[] getTrustManagers() {
        return null;
    }
}
