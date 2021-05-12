package com.jizhi.springboot.cache.repository.domain;

/**
 * 用户
 */
public class User {

    public User() {
    }

    public User(Integer id, Integer age, String name) {
       this.id = id;
       this.age = age;
       this.name = name;
    }

	/**
     * 主键
     */
    private Integer id;

	/**
     * 年龄
     */
    private Integer age;

	/**
     * 姓名
     */
    private String name;


	public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

	public void setAge(Integer age){
        this.age = age;
    }

    public Integer getAge(){
        return this.age;
    }

	public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }


    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Integer id;
        private Integer age;
        private String name;

    	public UserBuilder id(Integer id){
            this.id = id;
            return this;
        }

    	public UserBuilder age(Integer age){
            this.age = age;
            return this;
        }

    	public UserBuilder name(String name){
            this.name = name;
            return this;
        }

        public User build() {
            return new User(id, age, name);
        }
    }

}