package at.tfr.pfad.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrSubstitutor;

public class PositiveStrSubstitutor extends StrSubstitutor {

	public static final StrMatcher POSITIVE_DEFAULT_VALUE_DELIMITER = StrMatcher.stringMatcher(":+");
	public static final String SCRIPT_NAME = "js";
	protected ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	protected Map<String,?> valueMap = Collections.emptyMap();

	public PositiveStrSubstitutor() {
		super();
	}

	public <V> PositiveStrSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape,
			String valueDelimiter) {
		super(valueMap, prefix, suffix, escape, valueDelimiter);
		this.valueMap = valueMap;
	}

	public <V> PositiveStrSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape) {
		super(valueMap, prefix, suffix, escape);
		this.valueMap = valueMap;
	}

	public <V> PositiveStrSubstitutor(Map<String, V> valueMap, String prefix, String suffix) {
		super(valueMap, prefix, suffix);
		this.valueMap = valueMap;
	}

	public <V> PositiveStrSubstitutor(Map<String, V> valueMap) {
		super(valueMap);
		this.valueMap = valueMap;
	}

	public PositiveStrSubstitutor(StrLookup<?> variableResolver, String prefix, String suffix, char escape,
			String valueDelimiter) {
		super(variableResolver, prefix, suffix, escape, valueDelimiter);
	}

	public PositiveStrSubstitutor(StrLookup<?> variableResolver, String prefix, String suffix, char escape) {
		super(variableResolver, prefix, suffix, escape);
	}

	public PositiveStrSubstitutor(StrLookup<?> variableResolver, StrMatcher prefixMatcher, StrMatcher suffixMatcher,
			char escape, StrMatcher valueDelimiterMatcher) {
		super(variableResolver, prefixMatcher, suffixMatcher, escape, valueDelimiterMatcher);
	}

	public PositiveStrSubstitutor(StrLookup<?> variableResolver, StrMatcher prefixMatcher, StrMatcher suffixMatcher,
			char escape) {
		super(variableResolver, prefixMatcher, suffixMatcher, escape);
	}

	public PositiveStrSubstitutor(StrLookup<?> variableResolver) {
		super(variableResolver);
	}
	
	public PositiveStrSubstitutor withEngine(ScriptEngine engine) {
		this.engine = engine;
		return this;
	}
	
	public PositiveStrSubstitutor withValues(Map<String,?> valueMap) {
		this.valueMap = valueMap;
		return this;
	}
	
    //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source string as a template.
     *
     * @param source  the string to replace in, null returns null
     * @return the result of the replace operation
     */
    public String replace(final String source) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(source);
        if (substitute(buf, 0, source.length()) == false) {
            return source;
        }
        return buf.toString();
    }

    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source string as a template.
     * <p>
     * Only the specified portion of the string will be processed.
     * The rest of the string is not processed, and is not returned.
     *
     * @param source  the string to replace in, null returns null
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the array to be processed, must be valid
     * @return the result of the replace operation
     */
    public String replace(final String source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(length).append(source, offset, length);
        if (substitute(buf, 0, length) == false) {
            return source.substring(offset, offset + length);
        }
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source array as a template.
     * The array is not altered by this method.
     *
     * @param source  the character array to replace in, not altered, null returns null
     * @return the result of the replace operation
     */
    public String replace(final char[] source) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(source.length).append(source);
        substitute(buf, 0, source.length);
        return buf.toString();
    }

    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source array as a template.
     * The array is not altered by this method.
     * <p>
     * Only the specified portion of the array will be processed.
     * The rest of the array is not processed, and is not returned.
     *
     * @param source  the character array to replace in, not altered, null returns null
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the array to be processed, must be valid
     * @return the result of the replace operation
     */
    public String replace(final char[] source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source buffer as a template.
     * The buffer is not altered by this method.
     *
     * @param source  the buffer to use as a template, not changed, null returns null
     * @return the result of the replace operation
     */
    public String replace(final StringBuffer source) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source buffer as a template.
     * The buffer is not altered by this method.
     * <p>
     * Only the specified portion of the buffer will be processed.
     * The rest of the buffer is not processed, and is not returned.
     *
     * @param source  the buffer to use as a template, not changed, null returns null
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the array to be processed, must be valid
     * @return the result of the replace operation
     */
    public String replace(final StringBuffer source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source as a template.
     * The source is not altered by this method.
     *
     * @param source  the buffer to use as a template, not changed, null returns null
     * @return the result of the replace operation
     * @since 3.2
     */
    public String replace(final CharSequence source) {
        if (source == null) {
            return null;
        }
        return replace(source, 0, source.length());
    }

    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source as a template.
     * The source is not altered by this method.
     * <p>
     * Only the specified portion of the buffer will be processed.
     * The rest of the buffer is not processed, and is not returned.
     *
     * @param source  the buffer to use as a template, not changed, null returns null
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the array to be processed, must be valid
     * @return the result of the replace operation
     * @since 3.2
     */
    public String replace(final CharSequence source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source builder as a template.
     * The builder is not altered by this method.
     *
     * @param source  the builder to use as a template, not changed, null returns null
     * @return the result of the replace operation
     */
    public String replace(final StrBuilder source) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    /**
     * Replaces all the occurrences of variables with their matching values
     * from the resolver using the given source builder as a template.
     * The builder is not altered by this method.
     * <p>
     * Only the specified portion of the builder will be processed.
     * The rest of the builder is not processed, and is not returned.
     *
     * @param source  the builder to use as a template, not changed, null returns null
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the array to be processed, must be valid
     * @return the result of the replace operation
     */
    public String replace(final StrBuilder source, final int offset, final int length) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables in the given source object with
     * their matching values from the resolver. The input source object is
     * converted to a string using <code>toString</code> and is not altered.
     *
     * @param source  the source to replace in, null returns null
     * @return the result of the replace operation
     */
    public String replace(final Object source) {
        if (source == null) {
            return null;
        }
        final StrBuilder buf = new MyStrBuilder().append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables within the given source buffer
     * with their matching values from the resolver.
     * The buffer is updated with the result.
     *
     * @param source  the buffer to replace in, updated, null returns zero
     * @return true if altered
     */
    public boolean replaceIn(final StringBuffer source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    /**
     * Replaces all the occurrences of variables within the given source buffer
     * with their matching values from the resolver.
     * The buffer is updated with the result.
     * <p>
     * Only the specified portion of the buffer will be processed.
     * The rest of the buffer is not processed, but it is not deleted.
     *
     * @param source  the buffer to replace in, updated, null returns zero
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the buffer to be processed, must be valid
     * @return true if altered
     */
    public boolean replaceIn(final StringBuffer source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        final StrBuilder buf = new MyStrBuilder(length).append(source, offset, length);
        if (substitute(buf, 0, length) == false) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

  //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables within the given source buffer
     * with their matching values from the resolver.
     * The buffer is updated with the result.
     *
     * @param source  the buffer to replace in, updated, null returns zero
     * @return true if altered
     * @since 3.2
     */
    public boolean replaceIn(final StringBuilder source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    /**
     * Replaces all the occurrences of variables within the given source builder
     * with their matching values from the resolver.
     * The builder is updated with the result.
     * <p>
     * Only the specified portion of the buffer will be processed.
     * The rest of the buffer is not processed, but it is not deleted.
     *
     * @param source  the buffer to replace in, updated, null returns zero
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the buffer to be processed, must be valid
     * @return true if altered
     * @since 3.2
     */
    public boolean replaceIn(final StringBuilder source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        final StrBuilder buf = new MyStrBuilder(length).append(source, offset, length);
        if (substitute(buf, 0, length) == false) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Replaces all the occurrences of variables within the given source
     * builder with their matching values from the resolver.
     *
     * @param source  the builder to replace in, updated, null returns zero
     * @return true if altered
     */
    public boolean replaceIn(final StrBuilder source) {
        if (source == null) {
            return false;
        }
        return substitute(source, 0, source.length());
    }

    /**
     * Replaces all the occurrences of variables within the given source
     * builder with their matching values from the resolver.
     * <p>
     * Only the specified portion of the builder will be processed.
     * The rest of the builder is not processed, but it is not deleted.
     *
     * @param source  the builder to replace in, null returns zero
     * @param offset  the start offset within the array, must be valid
     * @param length  the length within the builder to be processed, must be valid
     * @return true if altered
     */
    public boolean replaceIn(final StrBuilder source, final int offset, final int length) {
        if (source == null) {
            return false;
        }
        return substitute(source, offset, length);
    }

    //-----------------------------------------------------------------------

	// -----------------------------------------------------------------------
	/**
	 * Internal method that substitutes the variables.
	 * <p>
	 * Most users of this class do not need to call this method. This method will be
	 * called automatically by another (public) method.
	 * <p>
	 * Writers of subclasses can override this method if they need access to the
	 * substitution process at the start or end.
	 *
	 * @param buf    the string builder to substitute into, not null
	 * @param offset the start offset within the builder, must be valid
	 * @param length the length within the builder to be processed, must be valid
	 * @return true if altered
	 */
	@Override
	protected boolean substitute(final StrBuilder buf, final int offset, final int length) {
		return substitute(buf, offset, length, null) > 0;
	}

	/**
	 * Recursive handler for multiple levels of interpolation. This is the main
	 * interpolation method, which resolves the values of all variable references
	 * contained in the passed in text.
	 *
	 * @param buf            the string builder to substitute into, not null
	 * @param offset         the start offset within the builder, must be valid
	 * @param length         the length within the builder to be processed, must be
	 *                       valid
	 * @param priorVariables the stack keeping track of the replaced variables, may
	 *                       be null
	 * @return the length change that occurs, unless priorVariables is null when the
	 *         int represents a boolean flag as to whether any change occurred.
	 */
	protected int substitute(final StrBuilder buf, final int offset, final int length, List<String> priorVariables) {
		final StrMatcher pfxMatcher = getVariablePrefixMatcher();
		final StrMatcher suffMatcher = getVariableSuffixMatcher();
		final char escape = getEscapeChar();
		final StrMatcher valueDelimMatcher = getValueDelimiterMatcher();
		final StrMatcher positiveDelimMatcher = POSITIVE_DEFAULT_VALUE_DELIMITER;
		final boolean substitutionInVariablesEnabled = isEnableSubstitutionInVariables();

		final boolean top = priorVariables == null;
		boolean altered = false;
		int lengthChange = 0;
		char[] chars = ((MyStrBuilder) buf).getBuffer();
		int bufEnd = offset + length;
		int pos = offset;
		while (pos < bufEnd) {
			final int startMatchLen = pfxMatcher.isMatch(chars, pos, offset, bufEnd);
			if (startMatchLen == 0) {
				pos++;
			} else {
				// found variable start marker
				if (pos > offset && chars[pos - 1] == escape) {
					// escaped
					buf.deleteCharAt(pos - 1);
					chars = ((MyStrBuilder) buf).getBuffer(); // in case buffer was altered
					lengthChange--;
					altered = true;
					bufEnd--;
				} else {
					// find suffix
					final int startPos = pos;
					pos += startMatchLen;
					int endMatchLen = 0;
					int nestedVarCount = 0;
					while (pos < bufEnd) {
						if (substitutionInVariablesEnabled
								&& (endMatchLen = pfxMatcher.isMatch(chars, pos, offset, bufEnd)) != 0) {
							// found a nested variable start
							nestedVarCount++;
							pos += endMatchLen;
							continue;
						}

						endMatchLen = suffMatcher.isMatch(chars, pos, offset, bufEnd);
						if (endMatchLen == 0) {
							pos++;
						} else {
							// found variable end marker
							if (nestedVarCount == 0) {
								String varNameExpr = new String(chars, startPos + startMatchLen,
										pos - startPos - startMatchLen);
								if (substitutionInVariablesEnabled) {
									final StrBuilder bufName = new MyStrBuilder(varNameExpr);
									substitute(bufName, 0, bufName.length());
									varNameExpr = bufName.toString();
								}
								pos += endMatchLen;
								final int endPos = pos;

								String varName = varNameExpr;
								String varDefaultValue = null;
								String varPositiveValue = null;

								if (positiveDelimMatcher != null) {
									final char[] varNameExprChars = varNameExpr.toCharArray();
									int valueDelimiterMatchLen = 0;
									for (int i = 0; i < varNameExprChars.length; i++) {
										// if there's any nested variable when nested variable substitution disabled,
										// then stop resolving name and default value.
										if (!substitutionInVariablesEnabled && pfxMatcher.isMatch(varNameExprChars, i,
												i, varNameExprChars.length) != 0) {
											break;
										}
										if ((valueDelimiterMatchLen = positiveDelimMatcher.isMatch(varNameExprChars,
												i)) != 0) {
											varName = varNameExpr.substring(0, i);
											varNameExpr = varNameExpr.substring(i + valueDelimiterMatchLen);
											varPositiveValue = varNameExpr;
											break;
										}
									}
								}

								if (valueDelimMatcher != null) {
									final char[] varNameExprChars = varNameExpr.toCharArray();
									int valueDelimiterMatchLen = 0;
									for (int i = 0; i < varNameExprChars.length; i++) {
										// if there's any nested variable when nested variable substitution disabled,
										// then stop resolving name and default value.
										if (!substitutionInVariablesEnabled && pfxMatcher.isMatch(varNameExprChars, i,
												i, varNameExprChars.length) != 0) {
											break;
										}
										if ((valueDelimiterMatchLen = valueDelimMatcher.isMatch(varNameExprChars,
												i)) != 0) {
											varName = varNameExpr.substring(0, i);
											varDefaultValue = varNameExpr.substring(i + valueDelimiterMatchLen);
											break;
										}
									}
								}

								// on the first call initialize priorVariables
								if (priorVariables == null) {
									priorVariables = new ArrayList<String>();
									priorVariables.add(new String(chars, offset, length));
								}

								// handle cyclic substitution
								checkCyclicSubstitution(varName, priorVariables);
								priorVariables.add(varName);

								// resolve the variable
								String varValue = resolveVariable(varName, buf, startPos, endPos);
								if (varValue == null || (varValue.length()==0 && varDefaultValue != null)) {
									if (SCRIPT_NAME.equals(varName)) {
										try {
											Bindings bindings = engine.createBindings();
											bindings.putAll(valueMap);
											varValue = ""+engine.eval(varDefaultValue, bindings);
										} catch (Throwable t) {
											throw new IllegalArgumentException("cannot eval: " + varName + ":" + varDefaultValue + " : " + t, t);
										}
									} else {
										varValue = varDefaultValue;
									}
								} else {
									if (varPositiveValue != null && !"false".equalsIgnoreCase(varValue)) {
										varValue = varPositiveValue;
									}
								}
								if (varValue != null) {
									// recursive replace
									final int varLen = varValue.length();
									buf.replace(startPos, endPos, varValue);
									altered = true;
									int change = substitute(buf, startPos, varLen, priorVariables);
									change = change + varLen - (endPos - startPos);
									pos += change;
									bufEnd += change;
									lengthChange += change;
									chars = ((MyStrBuilder)buf).getBuffer(); // in case buffer was
									// altered
								}

								// remove variable from the cyclic stack
								priorVariables.remove(priorVariables.size() - 1);
								break;
							}
							nestedVarCount--;
							pos += endMatchLen;
						}
					}
				}
			}
		}
		if (top) {
			return altered ? 1 : 0;
		}
		return lengthChange;
	}

	/**
	 * Checks if the specified variable is already in the stack (list) of variables.
	 *
	 * @param varName        the variable name to check
	 * @param priorVariables the list of prior variables
	 */
	protected void checkCyclicSubstitution(final String varName, final List<String> priorVariables) {
		if (priorVariables.contains(varName) == false) {
			return;
		}
		final StrBuilder buf = new MyStrBuilder(256);
		buf.append("Infinite loop in property interpolation of ");
		buf.append(priorVariables.remove(0));
		buf.append(": ");
		buf.appendWithSeparators(priorVariables, "->");
		throw new IllegalStateException(buf.toString());
	}

}
