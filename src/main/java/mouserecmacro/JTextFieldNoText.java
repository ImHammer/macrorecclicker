package mouserecmacro;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldNoText extends PlainDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int textLimit = -1;
	
	public JTextFieldNoText()
	{
		// TODO Auto-generated constructor stub
		this(-1);
	}
	
	public JTextFieldNoText(int textLimit) {
		this.textLimit = textLimit;
	}
	
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
	{
		// TODO Auto-generated method stub
		
		if (str == null || str.isEmpty()) {
			return;
		}
		
		String charactersString = "0123456789";
		
		for (int index = 0; index < str.length(); index++) {
			if (!charactersString.contains(String.valueOf(str.charAt(index)))) {
				return;
			}
		}
		
		if (this.textLimit > 0 && (getLength() + str.length()) > this.textLimit) {
			return;
		}
		
		super.insertString(offs, str, a);
	}
}
