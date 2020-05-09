# AutoCompleteSearchUsingRedisTemplate
Auto Complete Search from cache using RedisTemplate

Step 1 : Download redis-2.4.5-win32-win64.zip from https://redislabs.com/ebook/appendix-a/a-3-installing-on-windows/a-3-2-installing-redis-on-window/

Step 2 : Run redis-server.exe from \redis-2.4.5-win32-win64\64bit

Step 3 : Add Spring Boot dependencies (WEB, Spring Data Redis, Client Redis)

    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
    
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<!-- Spring client is also important -->
		<dependency>
			<groupId>redis.clients</groupId>
			<artifactId>jedis</artifactId>
			<version>3.1.0</version>
			<type>jar</type>
		</dependency>
    
Step 4 : Add redis server configuration to project in spring boot application java file.

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConFactory
				= new JedisConnectionFactory();
		jedisConFactory.setHostName("localhost");
		jedisConFactory.setPort(6379);
		jedisConFactory.getPoolConfig().setMaxIdle(30);
		jedisConFactory.getPoolConfig().setMinIdle(10);
		return jedisConFactory;
	}


	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		return template;
	}
 
Step 5 :  Create Rest Controller and add two api.

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @GetMapping("/add_word/{word}")
    String addStringToCache(@PathVariable("word") String word) {
        ListOperations<String,String> list = redisTemplate.opsForList();
        return String.valueOf(list.rightPush("searchedcachekey",word)) +" size : "+ String.valueOf(list.size("searchedcachekey"));
    }

    @GetMapping("/autocomplete/query/{key}")
    List<String> getStringFromCache(@PathVariable("key") String key) {
        List<String> resultStrings = redisTemplate.opsForList().range("searchedcachekey",0,10);

        //Using Java 8
        //List<String> strList = resultStrings.stream().filter(s -> s.startsWith(key)).collect(Collectors.toList());

        List<String> results2 =  new ArrayList<>();
        for(String s : resultStrings) {
            if(s.startsWith(key)) {
                results2.add(s);
            }
        }

        return results2;
    }
    
   
  Step 6 : To add string in cache 
  
  	http://localhost:8091/api/add_word/ab
  	http://localhost:8091/api/add_word/ab
  	http://localhost:8091/api/add_word/abc
  	http://localhost:8091/api/add_word/abcd
  	http://localhost:8091/api/add_word/abcde
  	http://localhost:8091/api/add_word/abcdef
  	http://localhost:8091/api/add_word/abcdefg
  
  Step 7 : To get String from cache for AutoComplete Search application
  
  	http://localhost:8091/api/autocomplete/query/abc
  
  Result would be : ["abc","abcd","abcde","abcdef","abcdefg"]
  
