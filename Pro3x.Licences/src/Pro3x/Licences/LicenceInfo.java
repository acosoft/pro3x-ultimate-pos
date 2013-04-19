package Pro3x.Licences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Aco
 */
public final class LicenceInfo
{
    protected String productCode;
    protected String productDescription;
    protected String licenceCode;
    protected String firstname;
    protected String lastname;
    protected String company;
    protected String email;
    protected String country;
    protected String city;
    protected String zipcode;
    protected Date validfrom;
    protected Date validto;
    protected HashMap<String, String> opcije = new HashMap<String, String>();

    LicenceInfo(String path) throws Exception
    {
        File licence = new File(path);
        Document xdoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(licence);
        
        this.productCode = getParam(xdoc, "pid");
        this.productDescription = getParam(xdoc, "pcode");
        this.licenceCode = getParam(xdoc, "refno");
        this.firstname = getParam(xdoc, "firstname");
        this.lastname = getParam(xdoc, "lastname");
        this.company = getParam(xdoc, "company");
        this.email = getParam(xdoc, "email");
        this.country = getParam(xdoc, "country");
        this.city = getParam(xdoc, "city");
        this.zipcode = getParam(xdoc, "zipcode");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        this.validfrom = sdf.parse(getParam(xdoc, "validfrom"));
        this.validto = sdf.parse(getParam(xdoc, "validto"));

        ReadOptions(xdoc);
    }

    private void ReadOptions(Document xdoc)
    {
        NodeList optionsQuery = xdoc.getElementsByTagName("options");
        if(optionsQuery.getLength() == 1)
        {
            Node optionsElement = optionsQuery.item(0);
            NodeList options = optionsElement.getChildNodes();

            int count = options.getLength();

            for(int i=0; i < count; i++)
            {
                Node option = options.item(i);
                opcije.put(option.getNodeName(), option.getTextContent());
            }
        }
    }

    public boolean verifyDate(Date datum)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datum);

        Calendar calfrom = Calendar.getInstance();
        calfrom.setTime(this.validfrom);

        Calendar calto = Calendar.getInstance();
        calto.setTime(this.validto);

        if(cal.after(calfrom) && cal.before(calto))
            return true;
        else
            return false;
    }

    public Date getValidto()
    {
        return validto;
    }

    public boolean verifyOption(String key, String value)
    {
        if(opcije.containsKey(key))
        {
            String vrijednost = opcije.get(key);
            return vrijednost.equals(value);
        }
        else
            return false;
    }
    
    public String readOption(String key, String fallback)
    {
        if(opcije.containsKey(key))
        {
            return opcije.get(key);
        }
        else
            return fallback;
    }

    public Date getValidfrom()
    {
        return validfrom;
    }

    private String getParam(Document xdoc, String name)
    {
        return xdoc.getElementsByTagName(name).item(0).getTextContent();
    }

    public String getZipcode()
    {
        return zipcode;
    }

    public String getCity()
    {
        return city;
    }

    public String getCountry()
    {
        return country;
    }

    public String getEmail()
    {
        return email;
    }

    public String getCompany()
    {
        return company;
    }

    public String getLastname()
    {
        return lastname;
    }

    public String getFirstname()
    {
        return firstname;
    }

    public String getLicenceCode()
    {
        return licenceCode;
    }

    public String getProductDescription()
    {
        return productDescription;
    }

    public String getProductCode()
    {
        return productCode;
    }
}
