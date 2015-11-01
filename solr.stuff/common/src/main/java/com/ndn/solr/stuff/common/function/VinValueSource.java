package com.ndn.solr.stuff.common.function;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VinValueSource extends ValueSource {
	public static final String IS_NOT_APPLY_COMMISION = "is_not_apply_commision";
	public static final String COMMISION_FEE = "commision_fee";
	public static final String START_TIME_DISCOUNT = "start_time_discount";
	public static final String FINISH_TIME_DISCOUNT = "finish_time_discount";
	public static final String IS_PROMOTION = "is_promotion";
	public static final String IS_PROMOTION_MAPPING = "is_promotion_mapping";
	public static final String PROMOTION_PRICE = "promotion_price";
	public static final String SELL_PRICE = "sell_price";
	
	public final Logger log = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
		NumericDocValues commisionBool = DocValues.getNumeric(readerContext.reader(), IS_NOT_APPLY_COMMISION);
		NumericDocValues commisionFeeNum = DocValues.getNumeric(readerContext.reader(), COMMISION_FEE);
		NumericDocValues startNum = DocValues.getNumeric(readerContext.reader(), START_TIME_DISCOUNT);
		NumericDocValues finishNum = DocValues.getNumeric(readerContext.reader(), FINISH_TIME_DISCOUNT);
		NumericDocValues isPromoBool = DocValues.getNumeric(readerContext.reader(), IS_PROMOTION);
		NumericDocValues isPromoMappingBool = DocValues.getNumeric(readerContext.reader(), IS_PROMOTION_MAPPING);
		NumericDocValues promoPriceNum = DocValues.getNumeric(readerContext.reader(), PROMOTION_PRICE);
		NumericDocValues sellPriceNum = DocValues.getNumeric(readerContext.reader(), SELL_PRICE);

		return new DoubleDocValues(this) {
			
			@Override
			public double doubleVal(int doc) {
				boolean isNotApplyCommision = commisionBool.get(doc) == 1;
				double commisionFee = Double.longBitsToDouble(commisionFeeNum.get(doc));
				long startPromo = startNum.get(doc);
				long finishPromo = finishNum.get(doc);
				boolean isPromo = isPromoBool.get(doc) == 1;
				boolean isPromoMapping = isPromoMappingBool.get(doc) == 1;
				double promoPrice = Double.longBitsToDouble(promoPriceNum.get(doc));
				double sellPrice = Double.longBitsToDouble(sellPriceNum.get(doc));
				
				log.info("isNotApplyCommision: {}", isNotApplyCommision);
				log.info("commisionFee: {}", commisionFee);
				log.info("startPromo: {}", startPromo);
				log.info("finishPromo: {}", finishPromo);
				log.info("isPromo: {}", isPromo);
				log.info("isPromoMapping: {}", isPromoMapping);
				log.info("promoPrice: {}", promoPrice);
				log.info("sellPrice: {}", sellPrice);
				
				long now = System.currentTimeMillis();
				double priceWithPromo = (isPromo && isPromoMapping && startPromo < now && finishPromo > now) ? promoPrice : sellPrice;				
				double finalPrice = isNotApplyCommision ? (priceWithPromo * (100 - commisionFee) / 100) : priceWithPromo;
				return finalPrice;
			}
		};
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String description() {
		return "return final price of adayroi";
	}
	
}
