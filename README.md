# Framework 2624

## Introduction

This library provides a lightweight framework for building web applications using annotations and dependency injection, similar to Spring. It includes features for request mapping, validation, dependency injection, and more.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Apache Tomcat 9 or higher
- Libraries:
  - `jakarta.servlet-api-5.0.0.jar`
  - `gson-2.8.8.jar`

### Building the Library

1. Clone the repository:
    ```sh
    git clone https://github.com/your-repo/framework_spring_web.git
    cd framework_spring_web
    ```

2. Build the library using the provided batch script:
    ```sh
    build.bat
    ```

3. The compiled JAR file will be located in the `build` directory and copied to `E:\dev\App-temoin-framework-spring\lib`.

### Adding the Library to Your Tomcat Project

1. Copy the generated JAR file (`framework_2624.jar`) from `E:\dev\App-temoin-framework-spring\lib` to the `WEB-INF/lib` directory of your Tomcat web application.

2. Ensure that your `web.xml` is configured to use the `FrontController` servlet. Here is an example configuration:

    ```xml
    <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                                 http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
             version="3.1">
    
        <servlet>
            <servlet-name>FrontController</servlet-name>
            <servlet-class>mg.itu.prom16.servlet.FrontController</servlet-class>
            <load-on-startup>1</load-on-startup>
        </servlet>
    
        <servlet-mapping>
            <servlet-name>FrontController</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>
    
        <context-param>
            <param-name>propertiesFile</param-name>
            <param-value>application.properties</param-value>
        </context-param>
    
    </web-app>
    ```

3. Create a `application.properties` file in the `WEB-INF/classes` directory with the following content:

    ```properties
    # Project Configuration
    projectPackage=com.yourcompany.yourproject
    controllerPackage=com.yourcompany.yourproject.controller

    # View Configuration
    viewFolder=/WEB-INF/views/
    suffixe=.jsp

    # Role Management
    roleSessionName=roleSession
    roleMatcher=mg.itu.prom16.role.StringRoleMatcher
    unauthorizedRedirectUrl=/unauthorized

    # Flash Attributes
    flashMapSession=flashDataAttribute

    ```

### Creating Controllers

1. Create a controller class and annotate it with `@Controller`. Define request mapping methods using `@GetMapping`, `@PostMapping`, and `@Url`.

    ```java
    package com.yourcompany.yourproject.controller;

    import mg.itu.prom16.annotation.controller.Controller;
    import mg.itu.prom16.annotation.mapping.GetMapping;
    import mg.itu.prom16.annotation.mapping.PostMapping;
    import mg.itu.prom16.annotation.mapping.Url;
    import mg.itu.prom16.response.ModelAndView;

    @Controller
    public class HomeController {

        @GetMapping
        @Url("/home")
        public ModelAndView home() {
            ModelAndView modelAndView = new ModelAndView("home");
            modelAndView.addObject("message", "Welcome to the Home Page!");
            return modelAndView;
        }

        @PostMapping
        @Url("/submit")
        public ModelAndView submit() {
            ModelAndView modelAndView = new ModelAndView("result");
            modelAndView.addObject("message", "Form submitted successfully!");
            return modelAndView;
        }
    }
    ```

### Dependency Injection

1. Use `@Component` to define beans and `@Autowired` to inject dependencies.

    ```java
    package com.yourcompany.yourproject.service;

    import mg.itu.prom16.annotation.Component;

    @Component
    public class MyService {
        public String getServiceMessage() {
            return "Service message";
        }
    }
    ```

    ```java
    package com.yourcompany.yourproject.controller;

    import mg.itu.prom16.annotation.Autowired;
    import mg.itu.prom16.annotation.controller.Controller;
    import mg.itu.prom16.annotation.mapping.GetMapping;
    import mg.itu.prom16.annotation.mapping.Url;
    import mg.itu.prom16.response.ModelAndView;
    import com.yourcompany.yourproject.service.MyService;

    @Controller
    public class ServiceController {

        @Autowired
        private MyService myService;

        @GetMapping
        @Url("/service")
        public ModelAndView service() {
            ModelAndView modelAndView = new ModelAndView("service");
            modelAndView.addObject("message", myService.getServiceMessage());
            return modelAndView;
        }
    }
    ```

2. Use `@Value` to inject values from properties files. You can use the `${}` syntax to reference properties.

    ```java
    package com.yourcompany.yourproject.config;

    import mg.itu.prom16.annotation.Component;
    import mg.itu.prom16.annotation.Value;

    @Component
    public class AppConfig {

        @Value("${app.name}")
        private String appName;

        public String getAppName() {
            return appName;
        }
    }
    ```

    ```java
    package com.yourcompany.yourproject.controller;

    import mg.itu.prom16.annotation.Autowired;
    import mg.itu.prom16.annotation.controller.Controller;
    import mg.itu.prom16.annotation.mapping.GetMapping;
    import mg.itu.prom16.annotation.mapping.Url;
    import mg.itu.prom16.response.ModelAndView;
    import com.yourcompany.yourproject.config.AppConfig;

    @Controller
    public class ConfigController {

        @Autowired
        private AppConfig appConfig;

        @GetMapping
        @Url("/config")
        public ModelAndView config() {
            ModelAndView modelAndView = new ModelAndView("config");
            modelAndView.addObject("appName", appConfig.getAppName());
            return modelAndView;
        }
    }
    ```

### Validation

1. Use validation annotations like `@NotBlank`, `@Email`, `@Size`, etc., to validate request parameters.

    ```java
    package com.yourcompany.yourproject.model;

    import mg.itu.prom16.annotation.validation.NotBlank;
    import mg.itu.prom16.annotation.validation.Email;

    public class User {

        @NotBlank(message = "Name cannot be blank")
        private String name;

        @Email(message = "Invalid email address")
        private String email;

        // Getters and setters
    }
    ```

    ```java
    package com.yourcompany.yourproject.controller;

    import mg.itu.prom16.annotation.controller.Controller;
    import mg.itu.prom16.annotation.mapping.PostMapping;
    import mg.itu.prom16.annotation.mapping.Url;
    import mg.itu.prom16.annotation.validation.Valid;
    import mg.itu.prom16.response.ModelAndView;
    import mg.itu.prom16.validation.BindingResult;
    import com.yourcompany.yourproject.model.User;

    @Controller
    public class UserController {

        @PostMapping
        @Url("/register")
        public ModelAndView register(@Valid User user, BindingResult bindingResult) {
            ModelAndView modelAndView = new ModelAndView("register");

            if (bindingResult.hasErrors()) {
                modelAndView.addObject("errors", bindingResult.getErrors());
                return modelAndView;
            }

            modelAndView.addObject("message", "User registered successfully!");
            return modelAndView;
        }
    }
    ```

### Running the Application

1. Deploy your web application to Tomcat.
2. Start Tomcat and navigate to your application in the browser.

## Conclusion

This guide provides a basic overview of how to use the framework. For more advanced usage and customization, refer to the source code and explore the various annotations and utilities provided by the library.
