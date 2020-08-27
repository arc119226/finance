package module.io
@Grab(group='commons-io', module='commons-io', version='2.5')
import org.apache.commons.io.FileUtils
class FileBetch{
	def storage = [:]

	def propertyMissing(String name) { 
		return storage[name] 
	}

	def methodMissing(String name, def args) {
		if(args.length==1){
			storage[name]=args[0]
		}else{
			storage[name]=args
		}
        return storage[name]
   }

	def executer(){
		if(clean){
			if(new File("${clean}").exists()){
				FileUtils.cleanDirectory(new File("${clean}"))
			}
		}
		if(delete){
			if(new File("${delete}").exists()){
				new File("${delete}").delete()
			}
		}
		if(errorlog){
            File error = new File('./error.log')
            error.append('\n'+errorlog)
		}
		if(log){
			print log
			def yyyymmdd=new Date().format('yyyyMMdd')
			File logmsg = new File("./${yyyymmdd}.log")
            logmsg.append(log)
		}
	}
	def static execute(@DelegatesTo(FileBetch) Closure block){
		FileBetch m = new FileBetch()
		block.delegate = m
		block()
		m.executer()
	}
}