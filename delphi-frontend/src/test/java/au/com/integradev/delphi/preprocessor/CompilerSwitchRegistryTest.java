/*
 * Sonar Delphi Plugin
 * Copyright (C) 2024 Integrated Application Development
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package au.com.integradev.delphi.preprocessor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.communitydelphi.api.directive.SwitchDirective.SwitchKind;

class CompilerSwitchRegistryTest {
  private CompilerSwitchRegistry registry;

  @BeforeEach
  void setup() {
    registry = new CompilerSwitchRegistry();
  }

  @Test
  void testAddAndQuerySwitch() {
    registry.addSwitch(SwitchKind.RANGECHECKS, 0, 10);
    assertThat(registry.isActiveSwitch(SwitchKind.RANGECHECKS, 5)).isTrue();
    assertThat(registry.isActiveSwitch(SwitchKind.RANGECHECKS, 15)).isFalse();
  }

  @Test
  void testPushAndPopState() {
    Map<SwitchKind, Integer> currentSwitches = new EnumMap<>(SwitchKind.class);
    currentSwitches.put(SwitchKind.RANGECHECKS, 0);
    currentSwitches.put(SwitchKind.IOCHECKS, 5);

    registry.pushState(currentSwitches);

    Map<SwitchKind, Integer> restored = registry.popState();
    assertThat(restored).isNotNull();
    assertThat(restored).containsEntry(SwitchKind.RANGECHECKS, 0);
    assertThat(restored).containsEntry(SwitchKind.IOCHECKS, 5);
  }

  @Test
  void testNestedPushAndPop() {
    Map<SwitchKind, Integer> firstState = new EnumMap<>(SwitchKind.class);
    firstState.put(SwitchKind.RANGECHECKS, 0);

    Map<SwitchKind, Integer> secondState = new EnumMap<>(SwitchKind.class);
    secondState.put(SwitchKind.IOCHECKS, 10);

    registry.pushState(firstState);
    registry.pushState(secondState);

    Map<SwitchKind, Integer> restored = registry.popState();
    assertThat(restored).containsEntry(SwitchKind.IOCHECKS, 10);
    assertThat(restored).doesNotContainKey(SwitchKind.RANGECHECKS);

    restored = registry.popState();
    assertThat(restored).containsEntry(SwitchKind.RANGECHECKS, 0);
    assertThat(restored).doesNotContainKey(SwitchKind.IOCHECKS);
  }

  @Test
  void testPopWithoutPushReturnsNull() {
    assertThat(registry.popState()).isNull();
  }

  @Test
  void testPushStateIsIndependentCopy() {
    Map<SwitchKind, Integer> currentSwitches = new EnumMap<>(SwitchKind.class);
    currentSwitches.put(SwitchKind.RANGECHECKS, 0);

    registry.pushState(currentSwitches);

    // Modify the original map after pushing
    currentSwitches.put(SwitchKind.IOCHECKS, 5);

    Map<SwitchKind, Integer> restored = registry.popState();
    assertThat(restored).doesNotContainKey(SwitchKind.IOCHECKS);
    assertThat(restored).containsEntry(SwitchKind.RANGECHECKS, 0);
  }
}
