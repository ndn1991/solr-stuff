package com.ndn.solr.stuff.common.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.SolrIndexSearcher;

import com.ndn.solr.stuff.common.Loggable;
import com.ndn.solr.stuff.common.eventdriven.Callable;
import com.ndn.solr.stuff.common.handler.worker.HandlerWorkerPool;
import com.ndn.solr.stuff.common.handler.worker.SolrParamsEvent;

public abstract class BaseHandler extends SearchHandler implements Loggable {
	private int poolSize = 4;
	private int poolBufferSize = 256;
	private HandlerWorkerPool pool = null;

	private SolrCore core;

	/**
	 * Hàm gốc của solr, cấm đè lần 2, hehe
	 * 
	 * @param req
	 * @param rsp
	 */
	@Override
	public final void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		try {
			process(req, rsp);
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	/**
	 * Hàm gốc của solr, cấm đè lần 2
	 * 
	 * @param core
	 */
	@Override
	public final void inform(SolrCore core) {
		this.core = core;
		core.addCloseHook(new CloseHook() {
			@Override
			public void preClose(SolrCore sc) {
				if (pool != null) {
					pool.stop();
				}
				getLogger().info("closing solr core...{}", this.getClass());
			}

			@Override
			public void postClose(SolrCore sc) {
				getLogger().info("closed solr core...{}", this.getClass());
			}
		});
		super.inform(core);

		try {
			inform(core.getResourceLoader());
		} catch (Exception e) {
			getLogger().error("", e);
		}
	}

	/**
	 * Hàm này dùng để thêm các tham số defaults và mặ định cho nó tiện
	 * 
	 * @param params
	 * @return
	 */
	protected SolrParams wrapDefaultsAndAppends(SolrParams params) {
		SolrParams result = params;
		if (defaults != null) {
			result = SolrParams.wrapDefaults(result, defaults);
		}
		if (appends != null) {
			result = SolrParams.wrapAppended(result, appends);
		}
		return result;
	}

	/**
	 * Gọi đến một handler bất kì của một core bất kì
	 * 
	 * @param coreName
	 *            tên core
	 * @param handlerName
	 *            tên handler, ví dụ "/select"
	 * @param req
	 *            request truyền vào
	 * @param rsp
	 *            response để chứa kết quả nhận vao
	 */
	public void executeHandler(String coreName, String handlerName, SolrQueryRequest req, SolrQueryResponse rsp) {
		this.core.getCoreDescriptor().getCoreContainer().getCore(coreName).getRequestHandler(handlerName)
				.handleRequest(req, rsp);
	}

	/**
	 * Gọi đến một handler bất kì của một core bất kì
	 * 
	 * @param core
	 *            core object
	 * @param handlerName
	 *            tên handler, ví dụ "/select"
	 * @param req
	 *            request truyền vào
	 * @param rsp
	 *            response để chứa kết quả nhận vao
	 */
	public void executeHandler(SolrCore core, String handlerName, SolrQueryRequest req, SolrQueryResponse rsp) {
		core.getRequestHandler(handlerName).handleRequest(req, rsp);
	}

	/**
	 * Gọi đến một handler bất kì của một core bất kì, Cái api này nó sẽ trả về
	 * response, thường dùng để thực hiện các request trung gian
	 * 
	 * @param coreName
	 *            tên core
	 * @param handlerName
	 *            tên handler, ví dụ "/select"
	 * @param params
	 *            tham số truyền vào
	 * @return
	 */
	public SolrQueryResponse executeHandler(String coreName, String handlerName, SolrParams params) {
		SolrQueryRequest req = new SolrQueryRequestBase(this.core, params) {
		};
		SolrQueryResponse res = new SolrQueryResponse();
		this.executeHandler(coreName, handlerName, req, res);
		return res;
	}

	/**
	 * Gọi đến một handler bất kì của một core bất kì, Cái api này nó sẽ trả về
	 * response, thường dùng để thực hiện các request trung gian
	 * 
	 * @param core
	 *            core object
	 * @param handlerName
	 *            tên handler, ví dụ "/select"
	 * @param params
	 *            tham số truyền vào
	 * @return
	 */
	public SolrQueryResponse executeHandler(SolrCore core, String handlerName, SolrParams params) {
		SolrQueryRequest req = new SolrQueryRequestBase(this.core, params) {
		};
		SolrQueryResponse res = new SolrQueryResponse();
		this.executeHandler(core, handlerName, req, res);
		return res;
	}

	/**
	 * Gọi đến một handler bất kì của một chính core hiện tại, Cái api này nó sẽ
	 * trả về response, thường dùng để thực hiện các request trung gian
	 * 
	 * @param handlerName
	 *            tên handler
	 * @param params
	 *            tham số truyền vào
	 * @return
	 */
	public SolrQueryResponse executeHandler(String handlerName, SolrParams params) {
		SolrQueryRequest req = new SolrQueryRequestBase(this.core, params) {
		};
		SolrQueryResponse res = new SolrQueryResponse();
		this.core.getRequestHandler(handlerName).handleRequest(req, res);
		return res;
	}

	/**
	 * Gọi đến một handler bất kì của một chính core hiện tại
	 * 
	 * @return
	 */
	public void executeHandler(String handlerName, SolrQueryRequest req, SolrQueryResponse rsp) {
		this.core.getRequestHandler(handlerName).handleRequest(req, rsp);
	}

	/**
	 * Gọi đến một handler bất kì của một core bất kì, chạy song song với một
	 * tập tham số. Lần đầu tiên gọi đến hàm này thì pool mới được tạo
	 * 
	 * @param coreName
	 * @param handlerName
	 * @param params
	 * @return
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	public List<NamedList> executeHandlerParallel(String coreName, String handlerName, List<SolrParams> params)
			throws InterruptedException {
		if (this.pool == null) {
			this.pool = new HandlerWorkerPool(this.poolSize, this.poolBufferSize, this);
			this.pool.start();
		}

		List<NamedList> nls = new CopyOnWriteArrayList<>();
		CountDownLatch latch = new CountDownLatch(params.size());
		List<SolrParamsEvent> evts = new ArrayList<>(params.size());
		for (SolrParams param : params) {
			SolrParamsEvent evt = new SolrParamsEvent();
			evt.setCore(coreName);
			evt.setHandler(handlerName);
			evt.setParams(param);
			evt.setCallBack(new Callable() {
				@Override
				public void call(Object... args) {
					NamedList result = (NamedList) args[0];
					if (result.size() != 0) {
						nls.add((NamedList) args[0]);
					}
					latch.countDown();
				}
			});
			evts.add(evt);
		}
		for (SolrParamsEvent evt : evts) {
			this.pool.publish(evt);
		}

		if (latch.getCount() != 0) {
			latch.await();
		}

		return nls;
	}

	/**
	 * Hàm tiện ích nhanh để lấy ra full thông tin document dưới dạng map
	 * 
	 * @param docs
	 * @param searcher
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, Object>> getDocsAsMap(DocList docs, SolrIndexSearcher searcher) throws IOException {
		List<Map<String, Object>> result = new ArrayList<>();
		DocIterator it = docs.iterator();
		while (it.hasNext()) {
			int id = it.next();
			Map<String, Object> map = new HashMap<>();
			Document doc = searcher.doc(id);
			for (IndexableField field : doc.getFields()) {
				Object[] os = doc.getValues(field.name());
				if (os != null) {
					if (os.length > 1) {
						List<String> tmpList = new ArrayList<String>();
						for (int i = 0; i < os.length; i++) {
							tmpList.add(os[i].toString());
						}
						map.put(field.name(), tmpList);
					} else {
						map.put(field.name(), os[0]);
					}
				}
			}
			result.add(map);
		}
		return result;
	}

	/**
	 * Hàm tiện ích nhanh để lấy ra full thông tin document dưới dạng map, và có
	 * giới hạn để lấy ra những field nào
	 * 
	 * @param docs
	 * @param searcher
	 * @param names
	 * @return
	 * @throws IOException
	 */
	public static List<Map<String, Object>> getDocsAsMap(DocList docs, SolrIndexSearcher searcher, Set<String> names)
			throws IOException {
		List<Map<String, Object>> result = new ArrayList<>();
		DocIterator it = docs.iterator();
		while (it.hasNext()) {
			int id = it.next();
			Map<String, Object> map = new HashMap<>();
			Document doc = searcher.doc(id);
			for (IndexableField field : doc.getFields()) {
				if (names.contains(field.name())) {
					Object[] os = doc.getValues(field.name());
					if (os != null) {
						if (os.length > 1) {
							List<String> tmpList = new ArrayList<String>();
							for (int i = 0; i < os.length; i++) {
								tmpList.add(os[i].toString());
							}
							map.put(field.name(), tmpList);
						} else {
							map.put(field.name(), os[0]);
						}
					}
				}
			}
			result.add(map);
		}
		return result;
	}

	/**
	 * Load tai nguyen
	 * 
	 * @param resourceLoader
	 * @throws Exception
	 */
	protected abstract void inform(SolrResourceLoader resourceLoader) throws Exception;

	/**
	 * Xu ly request va dong goi respone
	 * 
	 * @param req
	 * @param rsp
	 */
	protected abstract void process(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception;

	/**
	 * Đọc nhanh một file trong thư mục <b>conf</b> của một <b>core</b>
	 * 
	 * @param resourceLoader
	 * @param pathFile
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	protected final List<String> getResource(SolrResourceLoader resourceLoader, String pathFile)
			throws UnsupportedEncodingException, IOException {
		if (resourceLoader == null) {
			throw new RuntimeException("Ham inform chua duoc goi");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(resourceLoader.openResource(pathFile), "UTF-8"));
		List<String> res = new ArrayList<>();
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.trim().isEmpty()) {
				res.add(line);
			}
		}

		getLogger().info("Loaded resource from {} with {} lines", pathFile, res.size());

		return res;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getPoolBufferSize() {
		return poolBufferSize;
	}

	public void setPoolBufferSize(int poolBufferSize) {
		this.poolBufferSize = poolBufferSize;
	}
}
