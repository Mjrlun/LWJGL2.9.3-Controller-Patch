/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjgl.input;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.games.input.ControllerEnvironment;
import net.java.games.input.LinuxEnvironmentPlugin;
import net.java.games.input.OSXEnvironmentPlugin;

import org.lwjgl.LWJGLException;

import de.ralleytn.plugins.jinput.xinput.XInputEnvironmentPlugin;

/**
 * The collection of controllers currently connected.
 *
 * @author Kevin Glass
 * @Modder Ryley Avants
 */
public class Controllers {
	/** The controllers available */
	private static final HashMap<String, JInputController> controllers = new HashMap<String, JInputController>();

	/** The current list of events */
	private static final ArrayList<ControllerEvent> events = new ArrayList<ControllerEvent>();

	/** Whether controllers were created */
	private static boolean created = false;

	/** The currently assigned controller. */
	private static Controller currentController = null;

	/** This class' logger. */
	private static final Logger logger;

	/** Whether this class is in Single-Controller mode. */
	private static boolean monoControllerMode = false;

	/** The Current Controller's active events. */
	private static final ArrayList<ControllerEvent> currControllerEvents = new ArrayList<ControllerEvent>();

	/* CONTROLLER GENERICS AND FLEXIBILITY */

	/**
	 * Assigns a controller by the given name.
	 */
	public static boolean assignController(String name) {
		if (Controllers.controllers.size() > 0 && Controllers.controllers.containsKey(name)) {
			currentController = controllers.get(name);
			currControllerEvents.clear();
			return true;
		}
		return false;
	}

	/**
	 * Attempts to assign the Current Controller to the default.
	 */
	public static boolean assignDefaultController() {
		if (Controllers.controllers.size() == 0)
			return false;
		currentController = Controllers.controllers.values().iterator().next();
		return true;
	}

	/**
	 * Gets the number of available controllers.
	 */
	public static int getControllerCount() {
		return controllers.size();
	}

	/**
	 * Returns if the Current Controller exists.
	 */
	public static boolean currentControllerExists() {
		return currentController != null;
	}

	/**
	 * By setting state to TRUE, all calls to {@link #getAllEvents} will instead
	 * return {@link #getCurrentControllerEvents}.
	 */
	public static void enableMonoControllerMode(boolean state) {
		monoControllerMode = true;
	}

	/**
	 * Returns a Collection of all available controllers.
	 */
	public static List<JInputController> getControllers() {
		return Collections.unmodifiableList(new ArrayList<JInputController>(controllers.values()));
	}

	/**
	 * Returns a Collection of all available controller names.
	 */
	public static List<String> getControllerNames() {
		return Collections.unmodifiableList(new ArrayList<String>(controllers.keySet()));
	}

	/**
	 * Returns all current {@link ControllerEvent}s for the Current Controller.
	 */
	public static List<ControllerEvent> getCurrentControllerEvents() {
		if (currControllerEvents.size() == 0) {
			for (ControllerEvent event : events)
				if (event.getSource() == currentController)
					currControllerEvents.add(event);
		}
		return Collections.unmodifiableList(currControllerEvents);
	}

	/**
	 * Returns all current {@link ControllerEvent}s from all available Controllers.
	 * If monoControllerMode is enabled, will return
	 * {@link #getCurrentControllerEvents}
	 */
	public static List<ControllerEvent> getAllEvents() {
		if (monoControllerMode)
			return getCurrentControllerEvents();
		else
			return Collections.unmodifiableList(events);
	}

	/**
	 * Returns the current controller assigned by {@link #assignController}
	 */
	public static Controller getCurrentController() {
		return currentController;
	}

	/**
	 * Returns the name of the Current Controller.
	 */
	public static String getCurrentControllerName() {
		return currentController.getName();
	}

	/* CONTROLLER AXIS POLLING */

	/**
	 * Puts the requested stick's vector into the provided vec2.
	 */
	public static void getStickVector(Side side, float[] vec2) {
		switch (side) {
		case L:
			vec2[0] = currentController.getXAxisValue();
			vec2[1] = currentController.getYAxisValue();
			return;
		case R:
			vec2[0] = currentController.getRXAxisValue();
			vec2[1] = currentController.getRYAxisValue();
			return;
		}
	}

	public static float getTriggerState(Side side) {
		switch (side) {
		case L:
			return currentController.getZAxisValue();
		case R:
			return currentController.getRZAxisValue();
		}
		return 0F;
	}

	public static float getDPadState(Axis axis) {
		switch (axis) {
		case X:
			return currentController.getPovX();
		case Y:
			return currentController.getPovY();
		}
		return 0F;
	}

	/* GENERIC CONTROLLER AXIS POLLING */

	/**
	 * Puts the requested stick's vector into the provided vec2.
	 */
	public static void getStickVector(Controller controller, Side side, float[] vec2) {
		switch (side) {
		case L:
			vec2[0] = controller.getXAxisValue();
			vec2[1] = controller.getYAxisValue();
			return;
		case R:
			vec2[0] = controller.getRXAxisValue();
			vec2[1] = controller.getRYAxisValue();
			return;
		}
	}

	public static float getTriggerState(Controller controller, Side side) {
		switch (side) {
		case L:
			return controller.getZAxisValue();
		case R:
			return controller.getRZAxisValue();
		}
		return 0F;
	}

	public static float getDPadState(Controller controller, Axis axis) {
		switch (axis) {
		case X:
			return controller.getPovX();
		case Y:
			return controller.getPovY();
		}
		return 0F;
	}

	/* CONTROLLER TUNING METHODS */

	public static void setStickDeadzone(Side side, Axis axis, float amount) {
		switch (side) {
		case L:
			switch (axis) {
			case X:
				currentController.setXAxisDeadZone(clamp(amount));
				return;
			case Y:
				currentController.setYAxisDeadZone(clamp(amount));
				return;
			}
		case R:
			switch (axis) {
			case X:
				currentController.setRXAxisDeadZone(clamp(amount));
				return;
			case Y:
				currentController.setRYAxisDeadZone(clamp(amount));
				return;
			}
		}
	}

	public static void setTriggerDeadzone(Side side, float amount) {
		switch (side) {
		case L:
			currentController.setZAxisDeadZone(clamp(amount));
			return;
		case R:
			currentController.setRZAxisDeadZone(clamp(amount));
			return;
		}
	}

	public static void setControllerSettings(float leftXDeadzone, float leftYDeadzone, float rightXDeadzone, float rightYDeadzone, float leftTriggerDeadzone, float rightTriggerDeadzone) {
		setStickDeadzone(Side.L, Axis.X, leftXDeadzone);
		setStickDeadzone(Side.L, Axis.Y, leftYDeadzone);
		setStickDeadzone(Side.R, Axis.X, rightXDeadzone);
		setStickDeadzone(Side.R, Axis.Y, rightYDeadzone);
		setTriggerDeadzone(Side.L, leftTriggerDeadzone);
		setTriggerDeadzone(Side.R, rightTriggerDeadzone);
	}

	/* GENERIC CONTROLLER TUNING METHODS */

	public static void setStickDeadzone(Controller controller, Side side, Axis axis, float amount) {
		switch (side) {
		case L:
			switch (axis) {
			case X:
				controller.setXAxisDeadZone(clamp(amount));
				return;
			case Y:
				controller.setYAxisDeadZone(clamp(amount));
				return;
			}
		case R:
			switch (axis) {
			case X:
				controller.setRXAxisDeadZone(clamp(amount));
				return;
			case Y:
				controller.setRYAxisDeadZone(clamp(amount));
				return;
			}
		}
	}

	public static void setTriggerDeadzone(Controller controller, Side side, float amount) {
		switch (side) {
		case L:
			controller.setZAxisDeadZone(clamp(amount));
			return;
		case R:
			controller.setRZAxisDeadZone(clamp(amount));
			return;
		}
	}

	public static void setControllerSettings(Controller controller, float leftXDeadzone, float leftYDeadzone, float rightXDeadzone, float rightYDeadzone, float leftTriggerDeadzone, float rightTriggerDeadzone) {
		setStickDeadzone(controller, Side.L, Axis.X, leftXDeadzone);
		setStickDeadzone(controller, Side.L, Axis.Y, leftYDeadzone);
		setStickDeadzone(controller, Side.R, Axis.X, rightXDeadzone);
		setStickDeadzone(controller, Side.R, Axis.Y, rightYDeadzone);
		setTriggerDeadzone(controller, Side.L, leftTriggerDeadzone);
		setTriggerDeadzone(controller, Side.R, rightTriggerDeadzone);
	}

	/* CONTROLLER GET TUNING METHODS */

	public static float getStickDeadzone(Side side, Axis axis) {
		switch (side) {
		case L:
			switch (axis) {
			case X:
				return currentController.getXAxisDeadZone();
			case Y:
				return currentController.getYAxisDeadZone();
			}
		case R:
			switch (axis) {
			case X:
				return currentController.getRXAxisDeadZone();
			case Y:
				return currentController.getRYAxisDeadZone();
			}
		}
		return 0F;
	}

	public static float getTriggerDeadzone(Side side) {
		switch (side) {
		case L:
			return currentController.getZAxisDeadZone();
		case R:
			return currentController.getRZAxisDeadZone();
		}
		return 0F;
	}

	/* GENERIC CONTROLLER GET TUNING METHODS */

	public static float getStickDeadzone(Controller controller, Side side, Axis axis) {
		switch (side) {
		case L:
			switch (axis) {
			case X:
				return controller.getXAxisDeadZone();
			case Y:
				return controller.getYAxisDeadZone();
			}
		case R:
			switch (axis) {
			case X:
				return controller.getRXAxisDeadZone();
			case Y:
				return controller.getRYAxisDeadZone();
			}
		}
		return 0F;
	}

	public static float getTriggerDeadzone(Controller controller, Side side) {
		switch (side) {
		case L:
			return controller.getZAxisDeadZone();
		case R:
			return controller.getRZAxisDeadZone();
		}
		return 0F;
	}

	/* RUMBLER METHODS */

	/**
	 * Sets rumbling at the given strength.
	 */
	public static void rumbleController(String name, float strength) {
		currentController.setRumblerStrength(name, clamp(strength));
	}

	/**
	 * Sets rumbling at the given strength for the entire controller..
	 */
	public static void rumbleEntireController(float strength) {
		strength = clamp(strength);
		for (String s : currentController.getRumblerNames())
			currentController.setRumblerStrength(s, strength);
	}

	public static Collection<String> getRumblerNames() {
		return currentController.getRumblerNames();
	}

	/* GENERIC RUMBLER METHODS */

	/**
	 * Sets rumbling at the given strength.
	 */
	public static void rumbleController(Controller controller, String name, float strength) {
		controller.setRumblerStrength(name, clamp(strength));
	}

	/**
	 * Sets rumbling at the given strength for the entire controller..
	 */
	public static void rumbleEntireController(Controller controller, float strength) {
		strength = clamp(strength);
		for (String s : controller.getRumblerNames())
			controller.setRumblerStrength(s, strength);
	}

	public static Collection<String> getRumblerNames(Controller controller) {
		return controller.getRumblerNames();
	}

	/* AXIS ENUMS */

	public enum Side {
		L, R
	}

	public enum Axis {
		X, Y
	}

	/* INITIALIZATION AND POLLING */

	/**
	 * Update the collection of and poll the {@link Controller}s available.
	 * 
	 * @see org.lwjgl.opengl.Display#pollDevices
	 */
	public static void poll() {
		if (created) {
			Iterator<JInputController> iter = controllers.values().iterator();
			JInputController controller;
			String names = "";
			boolean didRemove = false;
			while (iter.hasNext())
				if (!(controller = iter.next()).poll()) {
					iter.remove();
					didRemove = true;
					names += controller.getName() + ", ";
				}
			if (didRemove)
				logger.warning("Removed unplugged/invalid controllers: " + names.substring(0, names.length() - 2) + ". If a wanted device is plugged back in, make sure to refresh devices.");
		}
	}

	/**
	 * Clears the List of {@link ControllerEvent}s. Call this once done using the
	 * events on that pass.
	 */
	public static void clearEvents() {
		events.clear();
		if (currControllerEvents.size() > 0)
			currControllerEvents.clear();
	}

	/**
	 * @return True if Controllers has been created
	 */
	public static boolean isCreated() {
		return created;
	}

	/**
	 * Add an event to the stack of events that have been caused
	 */
	static void addEvent(ControllerEvent event) {
		if (event != null) {
			events.add(event);
		}
	}

	/**
	 * Creates the JInput Controllers. Due to JInput limitations, to refresh
	 * controllers, call {@link #refreshDevices()}
	 */
	public static void create() throws LWJGLException {
		if (!created) {
			try {
				net.java.games.input.Controller[] found = getEnvironment().getControllers();
				for (net.java.games.input.Controller c : found) {
					if (c.getType() == net.java.games.input.Controller.Type.GAMEPAD)
						createController(c);
				}
			} catch (Throwable e) {
				throw new LWJGLException("Failed to initialize controllers.", e);
			}
			created = true;
		}
	}

	/**
	 * Utility to create a controller based on its potential sub-controllers
	 *
	 * @param c The controller to add
	 */
	private static void createController(net.java.games.input.Controller c) {
		net.java.games.input.Controller[] subControllers = c.getControllers();
		if (subControllers == null || subControllers.length == 0) {
			JInputController controller;
			controllers.put((controller = new JInputController(c)).getName(), controller);
		} else {
			for (net.java.games.input.Controller sub : subControllers) {
				createController(sub);
			}
		}
	}

	/**
	 * Refreshes the devices available. WARNING: Very expensive operation! Should be
	 * used sparingly.
	 */
	public static void refreshDevices() throws LWJGLException {
		if (created) {
			try {
				net.java.games.input.Controller[] found = getEnvironment().getControllers();
				for (net.java.games.input.Controller c : found) {
					if (!controllers.containsKey(c.getName()) && c.getType() == net.java.games.input.Controller.Type.GAMEPAD)
						createController(c);
				}
			} catch (Throwable e) {
				throw new LWJGLException("Failed to initialize controllers.", e);
			}
		}
	}

	private static Constructor<ControllerEnvironment> envConstructor;
	private static boolean customEnvironmentAssigned = false;

	/**
	 * Allows the API to register using other custom JInput plugins. Assign the
	 * constructor <b>before</b> calling {@link #create}
	 */
	public static void assignEnvironemnt(Constructor<? extends ControllerEnvironment> desiredConstructor) {
		envConstructor = (Constructor<ControllerEnvironment>) desiredConstructor;
	}

	private static ControllerEnvironment getEnvironment() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (customEnvironmentAssigned)
			return envConstructor.newInstance();
		else if (osName.startsWith("win"))
			return new XInputEnvironmentPlugin();
		else if (osName.equals("mac os x"))
			return new OSXEnvironmentPlugin();
		else if (osName.equals("linux"))
			return new LinuxEnvironmentPlugin();
		else
			return envConstructor.newInstance();
	}

	private static String osName;

	static {
		logger = Logger.getLogger(Controllers.class.getName());
		System.setProperty("jinput.loglevel","OFF");
		Logger.getLogger(ControllerEnvironment.class.getName()).setLevel(Level.OFF);
		try {
			osName = System.getProperty("os.name").toLowerCase().trim();
			if (osName.startsWith("win") || osName.equals("mac os x") || osName.equals("linux"))
				envConstructor = null;
			else
				(envConstructor = (Constructor<ControllerEnvironment>) ControllerEnvironment.getDefaultEnvironment().getClass().getDeclaredConstructors()[0]).setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* MISCELLANEOUS */

	private static float clamp(float f) {
		return f > 1.0F ? 1.0F : (f < -1.0F ? -1.0F : f);
	}
}
