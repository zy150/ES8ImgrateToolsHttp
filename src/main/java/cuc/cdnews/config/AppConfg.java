package cuc.cdnews.config;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan("cuc.cdnews.config") // 替换为实际包路径

@ComponentScan(basePackages = {
	    "cuc.cdnews.config",  // 扫描服务类
	    "cuc.cdnews.data"    // 扫描配置类
	})
public class AppConfg {
	@Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
		System.out.println("初始化");
		String OS_NAME = System.getProperty("os.name").toLowerCase();
		String extroConfig = "";
		if(OS_NAME.contains("win"))
		{
//			extroConfig="D:\\cdnewsfiles\\ES8ImgrateTools\\config.properties";
			extroConfig="classpath:application.properties";
		}
		else
		{
			extroConfig="/data/cdnewsfiles/ES8ImgrateTools/config.properties";
		}
		// 2. 修改这里的逻辑
		Resource resources;
		if (extroConfig.startsWith("classpath:")) {
			// 如果是 classpath 资源，就去掉前缀并使用 ClassPathResource
			String path = extroConfig.substring("classpath:".length());
			resources = new ClassPathResource(path);
		} else {
			// 否则，认为是文件系统资源，使用 FileSystemResource
			resources = new FileSystemResource(extroConfig);
		}
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//        Resource[] resources = null; 
//        try { 
//            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(); 
//            resources = resolver.getResources("classpath*:application.properties"); 
//        } catch (Exception e) { 
//            e.printStackTrace(); 
//        } 
//        Resource resources = new FileSystemResource(extroConfig);
        
        configurer.setLocations(resources); 
        configurer.setIgnoreUnresolvablePlaceholders(true); 
        return configurer; 
    } 
}
