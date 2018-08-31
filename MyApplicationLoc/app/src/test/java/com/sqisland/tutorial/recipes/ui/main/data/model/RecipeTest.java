package com.sqisland.tutorial.recipes.ui.main.data.model;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class RecipeTest {
    @Test
    public void water(){
        InputStream inputStream = RecipeTest.class.getResourceAsStream("/recipes/water.txt");
        Recipe recipe = Recipe.readFromStream(inputStream);
        assertNotNull(recipe);
        assertEquals("water",recipe.id);
        assertEquals("Water",recipe.title);
        assertEquals("Put glass under tap. Open tap. Close tap. Drink.",recipe.desc.toString());
    }

}