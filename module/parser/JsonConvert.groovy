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
		  .replaceAll('\\.','')
		  .replaceAll('\\$','')
		  .replaceAll('\\\\','')
		  .replaceAll('  ',' ')
		  .trim()
		  .replaceAll(' ','_')
		  .toLowerCase()
	}

	def integerNormalise ={it-> 
		it instanceof String?it.replaceAll(/^--$/,'0').replaceAll("\\'",'').replaceAll('X','').replaceAll(',','').replaceAll(/^-$/,'0').replaceAll('  ','').replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1$3$5'):it
	}

	def valueNormalise ={it-> 
		it instanceof String?it.replaceAll(/^--$/,'0').replaceAll("\\'",'').replaceAll('X','').replaceAll(',','').replaceAll(/^-$/,'0').replaceAll('  ','').replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1$3$5'):it
	}

	def static convert(@DelegatesTo(JsonConvert) Closure block){
        JsonConvert m = new JsonConvert()
        block.delegate = m
        block()
        return m.doParse()
    }
}


