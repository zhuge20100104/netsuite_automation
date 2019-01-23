package com.netsuite.base.page.actions.methods

class MethodKey {

    private String methodName
    private int paramSize
    private List<Object> paramTypes
    private List<String> readableParamTypes = new ArrayList<>()
    private Object page

    private Map<String,String> keyValueMap = new HashMap<>()


    MethodKey(MetaMethod method,Object page) {
        this.paramTypes = method.parameterTypes
        this.methodName = method.getName()
        this.paramSize = method.parameterTypes.size()
        this.page = page
        this.initParamTypeMap()
        this.readableParamTypes = this.toReadableParamTypes()
    }

    private void initParamTypeMap() {
        keyValueMap.put("int","int")
        keyValueMap.put("float","float")
        keyValueMap.put("class java.lang.String","string")
        keyValueMap.put("class java.lang.Object","object")
        keyValueMap.put("boolean","boolean")
        keyValueMap.put("long","long")
        keyValueMap.put("double","double")
    }

    private List<String> toReadableParamTypes() {
        paramTypes.each { paramType ->
            String strParamType = paramType.toString().trim()
            if(keyValueMap.containsKey(strParamType)) {
                readableParamTypes.add(keyValueMap.get(strParamType))
                //Customized types eg. [com.netsuite.base.People]
            }else{
                readableParamTypes.add("object")
            }
        }
        return readableParamTypes
    }

    String getMethodName() {
        return methodName
    }

    int getParamSize() {
        return paramSize
    }

    List<String> getReadableParamTypes() {
        return readableParamTypes
    }

    Object getPage() {
        return page
    }
}
