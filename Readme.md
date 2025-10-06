What you want to connect to  |  JDBC URL Example                                        |  Driver to Use
-----------------------------+----------------------------------------------------------+---------------
MySQL                        |  jdbc:mysql://localhost:3306/techbot?serverTimezone=UTC  |  MySQL        
PostgreSQL                   |  jdbc:postgresql://localhost:5432/techbot                |  PostgreSQL   



prompt :
String systemPrompt =
//                "Extract the following details from the user query: \n" +
//                        "- maximum price as a number,\n" +
//                        "- list of preferred brands,\n" +
//                        "- list of included features,\n" +
//                        "- list of excluded features,\n" +
//                        "- camera details,\n" +
//                        "- battery details,\n" +
//                        "- screen size in inches,\n" +
//                        "- display type,\n" +
//                        "- product generation,\n" +
//                        "- design style.\n" +
//                        "\n" +
//                        "If a certain attribute is not mentioned, return null or an empty list/appropriate default.\n" +
//                        "Provide the output strictly in JSON format matching the ProductFilter DTO structure.\n";


Controller               |  Endpoint               |  HTTP Method  |  Handler Method                     
-------------------------+-------------------------+---------------+-------------------------------------
ProductController        |  /api/products          |  GET          |  getProducts(String, Double)        
ChatbotController        |  /api/chatbot/parse     |  POST         |  parseUserPrompt(String)            
ProductSearchController  |  /api/products/search   |  GET          |  searchProducts(String)             
ProductController        |  /api/products          |  POST         |  createProduct(ProductDTO)          
ChatbotController        |  /api/chatbot/products  |  GET          |  getProductsReactive(String, Double)