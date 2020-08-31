package module.parser
class Regix{
	def pattern
	def document
	def groups

	def pattern(def pattern){
		this.pattern=pattern
	}
	def document(def document){
		this.document=document
	}
	def groups(def groups){
		this.groups=groups
	}


	def doParse(){
		def match = document =~ pattern
		match.find()
		def result = []
		match.each{data->
			result << data[groups]
		}
		return result
	}

	def static parse(@DelegatesTo(Regix) Closure block){
		Regix m = new Regix()
	    block.delegate = m
	    block()
	    return m.doParse()
    }
}