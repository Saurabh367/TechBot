mappings_json = '''
{
  "contexts": {
    "application": {
      "mappings": {
        "dispatcherServlets": {
          "dispatcherServlet": [
            {
              "handler": "com.techSuggestion.bot.controller.ProductController#getProducts",
              "details": {
                "requestMappingConditions": {
                  "patterns": ["/api/products"]
                }
              }
            },
            {
              "handler": "com.techSuggestion.bot.controller.ChatbotController#parseUserPrompt",
              "details": {
                "requestMappingConditions": {
                  "patterns": ["/api/chatbot/parse"]
                }
              }
            },
            {
              "handler": "com.techSuggestion.bot.controller.ProductSearchController#searchProducts",
              "details": {
                "requestMappingConditions": {
                  "patterns": ["/api/products/search"]
                }
              }
            }
          ]
        }
      }
    }
  }
}
'''
