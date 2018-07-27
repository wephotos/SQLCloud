package cn.sql.cloud.entity.meta;

import java.sql.DatabaseMetaData;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 数据库支持的数据类型信息
 * @author TQ
 *
 */
public class TypeInfo {

	/**
	 * 类型名称
	 */
	private String typeName;
	
	/**
	 * {@link java.sql.Types}
	 */
	private int dataType;
	
	/**
	 * 最大精度
	 */
	private int precision;
	/**
	 * 用于引用字面值的前缀（可为 null）
	 */
	private String literalPrefix;
	/**
	 * 用于引用字面值的后缀（可为 null）
	 */
	private String literalSuffix;
	/**
	 * 用于创建类型的参数（可为 null）
	 */
	private String createParams;
	/**
	 * 是否可对此类型使用 NULL
	 * {@link DatabaseMetaData#typeNoNulls}不可以
	 * {@link DatabaseMetaData#typeNullable}可以
	 * {@link DatabaseMetaData#typeNullableUnknown}不知道
	 */
	private short nullable;
	/**
	 * 是否区分大小写
	 */
	private boolean caseSensitive;
	/**
	 * 是否可以基于此类型使用 "WHERE"
	 * {@link DatabaseMetaData#typePredNone}不支持 
	 * {@link DatabaseMetaData#typePredChar}仅支持 WHERE ..LIKE 
	 * {@link DatabaseMetaData#typePredBasic}除 WHERE ..LIKE 以外都受支持 
	 * {@link DatabaseMetaData#typeSearchable}所有 WHERE ..都受支持 
	 */
	private short searchable;
	/**
	 * 是否不带符号
	 */
	private boolean unsignedAttribute;
	/**
	 * 是否可以为钱币值
	 */
	private boolean fixedPrecScale;
	/**
	 * 是否可以用于自动增量值
	 */
	private boolean autoIncrement;
	/**
	 * 类型名称的本地版（可为 null）
	 */
	private String localTypeName;
	/**
	 * 受支持的最小标度
	 */
	private short minimumScale;
	/**
	 * 受支持的最大标度
	 */
	private short maximumScale;
	/**
	 * 未被使用
	 */
	private int sqlDataType;
	/**
	 * 未被使用
	 */
	private int sqlDatetimeSub;
	/**
	 * 通常为 2 或 10 
	 */
	private int numPrecRadix;
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public String getLiteralPrefix() {
		return literalPrefix;
	}
	public void setLiteralPrefix(String literalPrefix) {
		this.literalPrefix = literalPrefix;
	}
	public String getLiteralSuffix() {
		return literalSuffix;
	}
	public void setLiteralSuffix(String literalSuffix) {
		this.literalSuffix = literalSuffix;
	}
	public String getCreateParams() {
		return createParams;
	}
	public void setCreateParams(String createParams) {
		this.createParams = createParams;
	}
	public short getNullable() {
		return nullable;
	}
	public void setNullable(short nullable) {
		this.nullable = nullable;
	}
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	public short getSearchable() {
		return searchable;
	}
	public void setSearchable(short searchable) {
		this.searchable = searchable;
	}
	public boolean isUnsignedAttribute() {
		return unsignedAttribute;
	}
	public void setUnsignedAttribute(boolean unsignedAttribute) {
		this.unsignedAttribute = unsignedAttribute;
	}
	public boolean isFixedPrecScale() {
		return fixedPrecScale;
	}
	public void setFixedPrecScale(boolean fixedPrecScale) {
		this.fixedPrecScale = fixedPrecScale;
	}
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	public String getLocalTypeName() {
		return localTypeName;
	}
	public void setLocalTypeName(String localTypeName) {
		this.localTypeName = localTypeName;
	}
	public short getMinimumScale() {
		return minimumScale;
	}
	public void setMinimumScale(short minimumScale) {
		this.minimumScale = minimumScale;
	}
	public short getMaximumScale() {
		return maximumScale;
	}
	public void setMaximumScale(short maximumScale) {
		this.maximumScale = maximumScale;
	}
	public int getSqlDataType() {
		return sqlDataType;
	}
	public void setSqlDataType(int sqlDataType) {
		this.sqlDataType = sqlDataType;
	}
	public int getSqlDatetimeSub() {
		return sqlDatetimeSub;
	}
	public void setSqlDatetimeSub(int sqlDatetimeSub) {
		this.sqlDatetimeSub = sqlDatetimeSub;
	}
	public int getNumPrecRadix() {
		return numPrecRadix;
	}
	public void setNumPrecRadix(int numPrecRadix) {
		this.numPrecRadix = numPrecRadix;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
}
