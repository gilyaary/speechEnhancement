package com.lg.audio.base;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IColorMappable;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class _3dTest {
	
	public void buildChart(Mapper mapper, Range xrange, int xsteps, Range yrange, int ysteps, double minBixCount, double maxBinCount){
		buildChart( mapper,  xrange,  xsteps,  yrange,  ysteps,  minBixCount,  maxBinCount,  "3D Histogram",  1200,  800);
	}
	public void buildChart(Mapper mapper, Range xrange, int xsteps, Range yrange, int ysteps, double minBixCount, double maxBinCount, String chartTitle, int width, int height){
		// Define range and precision for the function to plot
				

				// Create a surface drawing that function
				Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(xrange, xsteps, yrange, ysteps), mapper);
				//surface.getBounds().get
				ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow() , minBixCount ,maxBinCount);
				surface.setColorMapper(colorMapper );
				surface.setFaceDisplayed(true);
				surface.setWireframeDisplayed(false);
				surface.setWireframeColor(Color.BLACK);
								

				// Create a chart and add the surface
				Chart chart = new AWTChart(Quality.Advanced);
				chart.add(surface);
				chart.open(chartTitle, width, height);
	}
	
	public static void main(String[] args) {
		_3dTest _3dHist = new _3dTest();
		Mapper mapper = new Mapper() {
		    public double f(double x, double y) {
		        return 10 * Math.sin(x / 10) * Math.cos(y / 20);
		    }
		};
		Range xrange = new Range(-150, 150);
		int xsteps = 50;
		Range yrange = new Range(-150, 150);
		int ysteps = 50;
		_3dHist.buildChart(mapper, yrange, ysteps, yrange, ysteps, -30, 30);
	}
	
}
