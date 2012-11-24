package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.robot.SoundPlayer.RoboSound;

public class SoundPlayCommand extends Command {

	private RoboSound sound;

	public SoundPlayCommand(CommandType type) {
		super(type);
	}

	public SoundPlayCommand(CommandType type, RoboSound sound) {
		this(type);
		setSound(sound);
	}

	public RoboSound getSound() {
		return sound;
	}

	public void setSound(RoboSound sound) {
		this.sound = sound;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setSound(RoboSound.values()[dis.readInt()]);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeInt(sound.ordinal());
	}

}
