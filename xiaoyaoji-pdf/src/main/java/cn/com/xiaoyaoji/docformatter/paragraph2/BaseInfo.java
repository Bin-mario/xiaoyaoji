package cn.com.xiaoyaoji.docformatter.paragraph2;

import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;

public class BaseInfo {
	
	protected static BaseFont font;
	protected static BaseFont cjkFont;
	
	static {
		try {
			font = BaseFont.createFont("FZLTCXHJW.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			cjkFont = BaseFont.createFont("NotoSansCJKsc-Regular.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		} catch (DocumentException | IOException e) {
			throw new IllegalArgumentException("FONT not found", e);
		}
	}
}
