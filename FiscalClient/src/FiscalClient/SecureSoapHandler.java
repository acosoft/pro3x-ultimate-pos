/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FiscalClient;

import java.io.FileInputStream;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author aco
 */
public class SecureSoapHandler implements SOAPHandler<SOAPMessageContext> {

    private String keyStorePath;
    private String keyStorePassword;
    private String keyEntryPassword;
    
    private Key key;
    private Certificate certificate;

    public SecureSoapHandler(String keyStorePath, String keyStorePassword, String keyEntryPassword) throws Exception {
	this.keyStorePath = keyStorePath;
	this.keyStorePassword = keyStorePassword;
	this.keyEntryPassword = keyEntryPassword;
	
	prepareKeys();
    }
    
    private void prepareKeys() throws Exception
    {
	KeyStore store = KeyStore.getInstance("PKCS12");
	store.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());

	String alias = store.aliases().nextElement();
	key = store.getKey(alias, keyEntryPassword.toCharArray());
	
	certificate = store.getCertificate(alias);
    }
    
    @Override
    public Set<QName> getHeaders() {
	return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
	try {
	   
	    Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	    if(outbound.booleanValue() == true)
	    {
		Source contentSource = context.getMessage().getSOAPPart().getContent();

		Document xml = (Document) ((DOMSource)contentSource).getNode();
		Element node = (Element) xml.getDocumentElement().getFirstChild().getFirstChild();
		
		if(!node.getTagName().equals("EchoRequest"))
		{
		    DOMSignContext signContext = new DOMSignContext(key, node);
		    XMLSignatureFactory xmlSignFactory = XMLSignatureFactory.getInstance("DOM");
		    
		    Reference ref = xmlSignFactory.newReference("", xmlSignFactory.newDigestMethod(DigestMethod.SHA1, null),
			    Collections.singletonList(xmlSignFactory.newTransform(Transform.ENVELOPED,
			    (TransformParameterSpec) null)), null, null);

		    SignedInfo si = xmlSignFactory.newSignedInfo(xmlSignFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
			    (C14NMethodParameterSpec) null),
			    xmlSignFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
			    Collections.singletonList(ref));

		    KeyInfoFactory kif = xmlSignFactory.getKeyInfoFactory(); 
		    
		    X509Data x509 = kif.newX509Data(Collections.singletonList(certificate));
		    KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509));

		    XMLSignature signature = xmlSignFactory.newXMLSignature(si, ki); 

		    signature.sign(signContext);

		    TransformerFactory transform = TransformerFactory.newInstance();
		    Transformer transformer = transform.newTransformer();
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		    StringWriter data = new StringWriter();
		    StreamResult result = new StreamResult(data);
		    transformer.transform(contentSource, result);

		    System.out.println(data.toString());
		}
	    }
	} catch (Exception ex) {
	    //-Djavax.net.debug=all
	    Logger.getLogger(SecureSoapHandler.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
	//throw new UnsupportedOperationException("Not supported yet.");
	return true;
    }

    @Override
    public void close(MessageContext context) {
	//throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
