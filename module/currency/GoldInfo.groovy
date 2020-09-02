package module.currency
class GoldInfo{

	def doSync(){

	}
	def static sync(@DelegatesTo(GoldInfo) Closure block){
	        GoldInfo m = new GoldInfo()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}