package cn.sql.cloud.entity.meta;

/**
 * 虚拟目录层级
 * @author TQ
 *
 */
public class Virtual implements IMetaData {
	
	/**
	 * 名称
	 */
	private String name;
	
	public Virtual() {
		//
	}
	
	public Virtual(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getType() {
		return VIRTUAL;
	}

}
