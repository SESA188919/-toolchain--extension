/**
 * 
 * Copyright Assystem 2011
 * 
 * This file is part of "Mind Compiler" is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author  : Stéphane Seyvoz
 * Contact : sseyvoz@assystem.com 
 * Contributors : julien.tous@orange.com
 */

package org.ow2.mind.compilation.$toolchain$;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.ow2.mind.compilation.CompilerCommand;
import org.ow2.mind.compilation.CompilerErrors;
import org.ow2.mind.compilation.ExecutionHelper;
import org.ow2.mind.compilation.LinkerCommand;
import org.ow2.mind.compilation.AssemblerCommand;
import org.ow2.mind.compilation.PreprocessorCommand;
import org.ow2.mind.compilation.ExecutionHelper.ExecutionResult;
import org.ow2.mind.compilation.gcc.GccCompilerWrapper;

public class $TOOLCHAIN$CompilerWrapper extends GccCompilerWrapper {

	@Override
	public PreprocessorCommand newPreprocessorCommand(Map<Object, Object> context) {
		return new $TOOLCHAIN$PreprocessorCommand(context);
	}

	@Override
	public CompilerCommand newCompilerCommand(Map<Object, Object> context) {
		return new $TOOLCHAIN$CompilerCommand(context);
	}

	@Override
	public LinkerCommand newLinkerCommand(Map<Object, Object> context) {
		return new $TOOLCHAIN$LinkerCommand(context);
	}

	@Override
	public AssemblerCommand newAssemblerCommand(Map<Object, Object> context) {
		return new $TOOLCHAIN$AssemblerCommand(context);
	}
	// All subclasses are directly inspired from GccCompilerWrapper

	protected class $TOOLCHAIN$PreprocessorCommand extends GccPreprocessorCommand {

		protected $TOOLCHAIN$PreprocessorCommand(Map<Object, Object> context) {
			super(context);
		}

		public PreprocessorCommand addDebugFlag() {
			flags.add("--debug");
			return this;
		}


		public boolean exec() throws ADLException, InterruptedException {
			final List<String> cmd = new ArrayList<String>();
			cmd.add(this.cmd);

			cmd.addAll(flags);

			for (final String def : defines) {
				cmd.add("-D" + def);
			}
			for (final File incDir : includeDir) {
				cmd.add("-I" + incDir.getPath().trim());
			}

			for (final File incFile : includeFile) {
				cmd.add("--preinclude");
				cmd.add(incFile.getPath());
			}
			cmd.add("--dependencies=m");
			cmd.add(dependencyOutputFile.getPath());

			cmd.add("--preprocess=nl"); // already specifies output so we don't need -o
			//cmd.add("-o");
			cmd.add(outputFile.getPath());

			cmd.add(inputFile.getPath());

			// save full command for debug and log purposes
			final StringBuilder sb = new StringBuilder();
			for (final String str : cmd) {
				sb.append(str);
				sb.append(" ");
			}
			fullCmd = sb.toString();

			// execute command
			ExecutionResult result;
			try {
				result = ExecutionHelper.exec(getDescription(), cmd);
			} catch (final IOException e) {
				errorManagerItf.logError(CompilerErrors.EXECUTION_ERROR, this.cmd);
				return false;
			}
			if (dependencyOutputFile != null && dependencyOutputFile.exists()) {
				processDependencyOutputFile(dependencyOutputFile, context);
			}

			if (result.getExitValue() != 0) {
				errorManagerItf.logError(CompilerErrors.COMPILER_ERROR,
						outputFile.getPath(), result.getOutput());
				return false;
			}
			if (result.getOutput() != null) {
				// command returns 0 and generates an output (warning)
				errorManagerItf.logWarning(CompilerErrors.COMPILER_WARNING,
						outputFile.getPath(), result.getOutput());
			}
			return true;
		}

		public String getDescription() {
			return "CPP: " + outputFile.getPath();
		}
	}

	protected class $TOOLCHAIN$CompilerCommand extends GccCompilerCommand {

		protected $TOOLCHAIN$CompilerCommand(Map<Object, Object> context) {
			super(context);
		}

		// -g -> --debug
		public CompilerCommand addDebugFlag() {
			flags.add("--debug");
			return this;
		}

		public boolean exec() throws ADLException, InterruptedException {

			final List<String> cmd = new ArrayList<String>();
			cmd.add(this.cmd);

			// -c --> nothing because we already are in compiler
			// mode with icc whereas gcc is all integrated and
			// needs -c to compile only (without linking)
			//cmd.add("-c");

			cmd.addAll(flags);
			for (final String def : defines) {
				cmd.add("-D" + def);
			}
			for (final File incDir : includeDir) {
				cmd.add("-I" + incDir.getPath().trim());
			}

			for (final File incFile : includeFile) {
				cmd.add("--preinclude");
				cmd.add(incFile.getPath());
			}
			cmd.add("-o");
			cmd.add(outputFile.getPath());

			cmd.add(inputFile.getPath());

			// save full command for debug and log purposes
			final StringBuilder sb = new StringBuilder();
			for (final String str : cmd) {
				sb.append(str);
				sb.append(" ");
			}
			fullCmd = sb.toString();

			// execute command
			ExecutionResult result;
			try {
				result = ExecutionHelper.exec(getDescription(), cmd);
			} catch (final IOException e) {
				errorManagerItf.logError(CompilerErrors.EXECUTION_ERROR, this.cmd);
				return false;
			}
			if (dependencyOutputFile != null && dependencyOutputFile.exists()) {
				processDependencyOutputFile(dependencyOutputFile, context);
			}

			if (result.getExitValue() != 0) {
				errorManagerItf.logError(CompilerErrors.COMPILER_ERROR,
						outputFile.getPath(), result.getOutput());
				return false;
			}
			if (result.getOutput() != null) {
				// command returns 0 and generates an output (warning)
				errorManagerItf.logWarning(CompilerErrors.COMPILER_WARNING,
						outputFile.getPath(), result.getOutput());
			}
			return true;
		}

		public String getDescription() {
			return "ICC: " + outputFile.getPath();

		}
	}

	protected class $TOOLCHAIN$LinkerCommand extends GccLinkerCommand {

		protected $TOOLCHAIN$LinkerCommand(Map<Object, Object> context) {
			super(context);
		}

		// doesn't exist with XLINK : Do nothing
		public LinkerCommand addDebugFlag() {
			//flags.add("-g");
			return this;
		}

		public boolean exec() throws ADLException, InterruptedException {
			final List<String> cmd = new ArrayList<String>();
			cmd.add(this.cmd);

			cmd.add("-o");
			cmd.add(outputFile.getPath());

			// NOTE SPECIFIC TO $TOOLCHAIN$ : .a files DO NOT EXIST but this lines adds
			// input files anyway

			// archive files (i.e. '.a' files) are added at the end of the command
			// line.
			List<String> archiveFiles = null;
			for (final File inputFile : inputFiles) {
				final String path = inputFile.getPath();
				if (path.endsWith(".a")) {
					if (archiveFiles == null) archiveFiles = new ArrayList<String>();
					archiveFiles.add(path);
				} else {
					cmd.add(path);
				}
			}
			if (archiveFiles != null) {
				for (final String path : archiveFiles) {
					cmd.add(path);
				}
			}
			// Linker scripts do not exist in $TOOLCHAIN$ -> removed -T	        

			cmd.addAll(flags);

			// save full command for debug and log purposes
			final StringBuilder sb = new StringBuilder();
			for (final String str : cmd) {
				sb.append(str);
				sb.append(" ");
			}
			fullCmd = sb.toString();

			// execute command
			ExecutionResult result;
			try {
				result = ExecutionHelper.exec(getDescription(), cmd);
			} catch (final IOException e) {
				errorManagerItf.logError(CompilerErrors.EXECUTION_ERROR, this.cmd);
				return false;
			}

			if (result.getExitValue() != 0) {
				errorManagerItf.logError(CompilerErrors.LINKER_ERROR,
						outputFile.getPath(), result.getOutput());
				return false;
			}
			if (result.getOutput() != null) {
				// command returns 0 and generates an output (warning)
				errorManagerItf.logWarning(CompilerErrors.LINKER_WARNING,
						outputFile.getPath(), result.getOutput());
			}
			return true;
		}

		public String getDescription() {
			return "LINK : " + outputFile.getPath();
		}
	}

	protected class $TOOLCHAIN$AssemblerCommand extends GccAssemblerCommand {

		protected $TOOLCHAIN$AssemblerCommand(Map<Object, Object> context) {
			super(context);
		}

		public AssemblerCommand addDebugFlag() {
			flags.add("-r");
			return this;
		}

		public boolean exec() throws ADLException, InterruptedException {

			final List<String> cmd = new ArrayList<String>();
			cmd.add(this.cmd);

			cmd.addAll(flags);

			cmd.add("-o");
			cmd.add(outputFile.getPath());

			cmd.add(inputFile.getPath());

			// save full command for debug and log purposes
			final StringBuilder sb = new StringBuilder();
			for (final String str : cmd) {
				sb.append(str);
				sb.append(" ");
			}
			fullCmd = sb.toString();

			// execute command
			ExecutionResult result;
			try {
				result = ExecutionHelper.exec(getDescription(), cmd);
			} catch (final IOException e) {
				errorManagerItf.logError(CompilerErrors.EXECUTION_ERROR, this.cmd);
				return false;
			}

			if (result.getExitValue() != 0) {
				errorManagerItf.logError(CompilerErrors.ASSEMBLER_ERROR,
						outputFile.getPath(), result.getOutput());
				return false;
			}
			if (result.getOutput() != null) {
				// command returns 0 and generates an output (warning)
				errorManagerItf.logWarning(CompilerErrors.ASSEMBLER_WARNING,
						outputFile.getPath(), result.getOutput());
			}
			return true;
		}
	}	

}