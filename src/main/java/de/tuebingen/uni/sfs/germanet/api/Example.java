/*
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
 *
 * This file is part of the Java API to GermaNet.
 *
 * The Java API to GermaNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java API to GermaNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this API; if not, see <http://www.gnu.org/licenses/>.
 */
package de.tuebingen.uni.sfs.germanet.api;

/**
 * An <code>Example</code> consists of a text <code>String</code> and an
 * optional <code>Frame</code>.
 * 
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at sfs.uni-tuebingen.de)
 * @version 13.0
 */
public class Example {
    private String text;
    private Frame frame = null;
    private String llmModel;
    private String llmVersion;
    private String dateCreated;
    private Boolean edited;

    /**
     * Constructs a new <code>Example</code> with empty text and no
     * <code>Frame</code>.
     */
    protected Example() {
        this.text = "";
        this.llmModel = null;
		this.llmVersion = null;
		this.dateCreated = null;
		this.edited = false;
    }

    /**
     * Constructs a new <code>Example</code> with the specified text and no
     * <code>Frame</code>.
     * @param text the text for this <code>Example</code>
     */
    protected Example(String text) {
        this.text = text;
    }

	/**
     * Replaces the text with the specified text.
     * @param text the new text
     */
    protected void setText(String text) {
        this.text = text;
    }

    /**
     * Set the specified <code>Frame</code> to this <code>Example</code>.
     * @param frame the <code>Frame</code> to set for this <code>Example</code>
     */
    protected void setFrame(Frame frame) {
        this.frame = frame;
    }

    /**
     * Return this <code>Example</code>'s text.
     * @return this <code>Example</code>'s text.
     */
    public String getText() {
        return text;
    }

    /**
     * Return the <code>Frame</code> as <code>String</code> of this
     * <code>Example</code>.
     * @return the <code>Frame</code> as <code>String</code> of this
     * <code>Example</code>.
     */
    public String getFrame() {
        if (this.frame != null) {
            return frame.getData();
        } else {
            return null;
        }
    }

    /**
	 * @return the llmModel
	 */
	public String getLlmModel() {
		return llmModel;
	}

	/**
	 * @param llmModel the llmModel to set
	 */
	public void setLlmModel(String llmModel) {
		this.llmModel = llmModel;
	}

	/**
	 * @return the llmVersion
	 */
	public String getLlmVersion() {
		return llmVersion;
	}

	/**
	 * @param llmVersion the llmVersion to set
	 */
	public void setLlmVersion(String llmVersion) {
		this.llmVersion = llmVersion;
	}

	/**
	 * @return the dateCreated
	 */
	public String getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the edited
	 */
	public Boolean getEdited() {
		return edited;
	}

	/**
	 * @param edited the edited to set
	 */
	public void setEdited(Boolean edited) {
		this.edited = edited;
	}

	/**
     * Return a String representation of this <code>Example</code>.
     * @return a String representation of this <code>Example</code>.
     */
    @Override
    public String toString() {
        String out = text;
        if (frame != null) {
            out += ", frame: " + frame.getData();
        }
        return out;
    }
}
