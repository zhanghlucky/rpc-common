package com.hui.zhang.common.util.performance.model;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 * 任务优先级
 */
public enum TaskPriority {

	HIGHT(1000),
	NORMAL(500),
	LOW(100);

	private int level;

	private TaskPriority(int level){
		this.level = level;
	}

	public int getLevel(){
		return level;
	}

}
