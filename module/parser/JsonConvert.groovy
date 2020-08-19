package module.parser
class JsonConvert{

	String input
	def parseRule

	def input(String input){
		this.input=input
	}
	def parseRule(def parseRule){
		this.parseRule=parseRule
	}
	
	def doParse(){
		def json = new groovy.json.JsonSlurper().parseText(input)
		return  parseRule.call(json)
	}
	def fieldNormalize = {it ->
		it.replaceAll('\\(','')
		  .replaceAll('\\)','')
		  .replaceAll('\\%','')
		  .replaceAll('\\/','')
		  .replaceAll('  ',' ')
		  .trim()
		  .replaceAll(' ','_')
		  .toLowerCase()
	}

	def integerNormalise ={it-> 
		it instanceof String?it.replaceAll(/^--$/,'0').replaceAll('X','').replaceAll(',','').replaceAll(/^-$/,'0').replaceAll('  ',''):it
	}

	def valueNormalise ={it-> 
		it instanceof String?it.replaceAll(/^--$/,'0').replaceAll('X','').replaceAll(',','').replaceAll(/^-$/,'0').replaceAll('  ',''):it
	}

	def static convert(@DelegatesTo(JsonConvert) Closure block){
        JsonConvert m = new JsonConvert()
        block.delegate = m
        block()
        return m.doParse()
    }
}


