package com.netsuite.base.page.actions

class ActionEntity {

    String entityContent

    ActionEntity(String entityContent){
        this.entityContent = entityContent
        this.toEntity()

    }

    List<String> entities = new ArrayList<>()



    def getDoubleColonArray(String entityContent) {
        String [] contentArr = null
        entityContent = entityContent.replace("::","`")
        contentArr = entityContent.split(":")
        int i = 0
        contentArr.each {
            if(it.contains("`")) {
                contentArr[i] = it.replace("`",":")
            }
            i++
        }
        return contentArr
    }


    def toEntity() {


        if(entityContent==null || entityContent.isEmpty()) {
            return
        }


        String [] contentArr = null

        if(entityContent.contains("::")) {
            contentArr = getDoubleColonArray(entityContent)
        }else {
            contentArr = entityContent.split(":")
        }

        entities = Arrays.asList(contentArr)

        return entities
    }


    def getEntity() {
        return this.entities
    }

    def AddEntityKey(def key) {
        this.entities.add(key)
    }


    @Override
    public String toString() {
        println(this.entities)
    }

    static void main(String [] args) {
        def entity = new ActionEntity("filter1:page1:true")

        println(entity.getEntities())

    }
}
