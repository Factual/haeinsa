package kr.co.vcnc.haeinsa;

import java.util.List;

import kr.co.vcnc.haeinsa.thrift.generated.TRowLock;

import com.google.common.collect.Lists;

/**
 * Contains Transaction information of single row.
 * This information is only saved in client memory until {@link HaeinsaTransaction#commit()} called.
 * @author Myungbo Kim
 *
 */
class HaeinsaRowTransaction {
	private TRowLock current;
	private final List<HaeinsaMutation> mutations = Lists.newArrayList();
	private final HaeinsaTableTransaction tableTransaction;
	
	HaeinsaRowTransaction(HaeinsaTableTransaction tableTransaction) {
		this.tableTransaction = tableTransaction;
	}
	
	public TRowLock getCurrent() {
		return current;
	}

	public void setCurrent(TRowLock current) {
		this.current = current;
	}

	public List<HaeinsaMutation> getMutations() {
		return mutations;
	}
	
	public int getIterationCount(){
		if (mutations.size() > 0){
			if (mutations.get(0) instanceof HaeinsaPut){
				return mutations.size();
			}else{
				return mutations.size() + 1;
			}
		}
		return 1;
	}
	
	public void addMutation(HaeinsaMutation mutation) {
		if (mutations.size() <= 0){
			mutations.add(mutation);
		} else {
			HaeinsaMutation lastMutation = mutations.get(mutations.size() - 1);
			if (lastMutation.getClass() != mutation.getClass()){
				mutations.add(mutation);
			}else{
				lastMutation.add(mutation);
			}
		}
	}
	
	public HaeinsaTableTransaction getTableTransaction() {
		return tableTransaction;
	}
	
	public List<HaeinsaKeyValueScanner> getScanners() {
		List<HaeinsaKeyValueScanner> result = Lists.newArrayList();
		for (int i=0;i<mutations.size();i++){
			HaeinsaMutation mutation = mutations.get(i);
			result.add(mutation.getScanner(mutations.size() - i));
		}
		
		return result;
	}
}