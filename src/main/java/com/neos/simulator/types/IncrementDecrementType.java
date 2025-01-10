package com.neos.simulator.types;

import java.util.ArrayList;
import java.util.List;

public abstract class IncrementDecrementType extends TypeHandler{
	private TypeHandler typeHandler;
	private double baseDouble;
	private long baseLong;
	private boolean isLongType;
	private boolean isIncrementType;
	private boolean isFixedType;

	public void setArguments(boolean isIncrementType, boolean isFixedType, List<Object> args) {
		this.isIncrementType = isIncrementType;
		this.isFixedType = isFixedType;
		Object base = args.get(0);

		if (this.isFixedType) {
			Object constant = args.get(1);
			List<Object> otherArgs = new ArrayList<>();			
			
			isLongType = true;
			if (base instanceof Double || constant instanceof Double) {
				isLongType = false;
			}
			
			if (isLongType) {
				otherArgs.add(convertToLong(constant));
			}else {
				otherArgs.add(convertToDouble(constant));
			}		
			
			typeHandler = new ConstantType();
			typeHandler.setArguments(otherArgs);
		} else {
			Object min = args.get(1);
			Object max = args.get(2);
			List<Object> otherArgs = new ArrayList<>();			
			
			isLongType = true;
			if (base instanceof Double || min instanceof Double || max instanceof Double) {
				isLongType = false;
			}
			
			if (isLongType) {
				otherArgs.add(convertToLong(min));
				otherArgs.add(convertToLong(max));
				typeHandler = new LongType();
				typeHandler.setArguments(otherArgs);
			} else {
				if (min instanceof Long && max instanceof Long) {
					otherArgs.add((long) min);
					otherArgs.add((long) max);
					typeHandler = new LongType();
					typeHandler.setArguments(otherArgs);
				}else if (min instanceof Integer && max instanceof Integer) {
					otherArgs.add((long) (int) min);
					otherArgs.add((long) (int) max);
					typeHandler = new LongType();
					typeHandler.setArguments(otherArgs);
				}
				else {
					otherArgs.add(convertToDouble(min));
					otherArgs.add(convertToDouble(max));					
					typeHandler = new DoubleType();
					typeHandler.setArguments(otherArgs);
				}				
			}
		}
		
		 if (isLongType) {
	            baseLong = convertToLong(base);
	        } else {
	            baseDouble = convertToDouble(base);
	        }
		
	}

	
	 private long convertToLong(Object value) {
	        if (value instanceof Long) {
	            return (long) value;
	        } else if (value instanceof Integer) {
	            return (int) value;
	        } else {
	            return (long) (double) value;
	        }
	    }

	    private double convertToDouble(Object value) {
	        if (value instanceof Double) {
	            return (double) value;
	        } else if (value instanceof Long) {
	            return (double) (long) value;
	        } else {
	            return (double) (int) value;
	        }
	    }
	    

	@Override
	public Object getValue() {
		 Object val = typeHandler.getValue();
	        if (isLongType) {
	            long longVal = (long) val;
	            baseLong = isIncrementType ? baseLong + longVal : baseLong - longVal;
	            return baseLong;
	        } else {
	            double doubleVal = val instanceof Double ? (double) val : (double) (long) val;
	            baseDouble = isIncrementType ? baseDouble + doubleVal : baseDouble - doubleVal;
	            return baseDouble;
	        }
    }
}
