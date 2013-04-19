/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 *
 * @author aco
 */
public class SimpleDecimal extends BigDecimal
{
    public SimpleDecimal(long val, MathContext mc) {
        super(val, mc);
    }

    public SimpleDecimal(long val) {
        super(val);
    }

    public SimpleDecimal(int val, MathContext mc) {
        super(val, mc);
    }

    public SimpleDecimal(int val) {
        super(val);
    }

    public SimpleDecimal(BigInteger unscaledVal, int scale, MathContext mc) {
        super(unscaledVal, scale, mc);
    }

    public SimpleDecimal(BigInteger unscaledVal, int scale) {
        super(unscaledVal, scale);
    }

    public SimpleDecimal(BigInteger val, MathContext mc) {
        super(val, mc);
    }

    public SimpleDecimal(BigInteger val) {
        super(val);
    }

    public SimpleDecimal(double val, MathContext mc) {
        super(val, mc);
    }

    public SimpleDecimal(double val) {
        super(val);
    }

    public SimpleDecimal(String val, MathContext mc) {
        super(val, mc);
    }

    public SimpleDecimal(String val) {
        super(val);
    }

    public SimpleDecimal(char[] in, MathContext mc) {
        super(in, mc);
    }

    public SimpleDecimal(char[] in) {
        super(in);
    }

    public SimpleDecimal(char[] in, int offset, int len, MathContext mc) {
        super(in, offset, len, mc);
    }

    public SimpleDecimal(char[] in, int offset, int len) {
        super(in, offset, len);
    }

    @Override
    public String toPlainString() {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance();
        
        nf.setGroupingUsed(false);
        
        DecimalFormatSymbols simbols = new DecimalFormatSymbols();
        simbols.setDecimalSeparator('.');
        
        nf.setDecimalFormatSymbols(simbols);
        
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        
        String value = nf.format(doubleValue());
        return value;
    }
}
